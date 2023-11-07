from flask import Flask, jsonify, abort
from flask_restful import Resource, Api, reqparse
from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity
import bcrypt
from models import db, User, Loan, Repayment, Transaction
from config import config_by_name
from validate import validate_due_date, validate_loan_repay
from datetime import datetime
from uuid import uuid4

app = Flask(__name__)
api = Api(app)
jwt = JWTManager(app)


def create_app(config_name):
    app.config.from_object(config_by_name[config_name])

    with app.app_context():
        db.init_app(app)
        db.create_all()

    return app


class Register(Resource):
    def post(self):
        auth_parser = reqparse.RequestParser()
        auth_parser.add_argument('username', type=str, required=True,
                                 help="username is required and must be a string")
        auth_parser.add_argument('password', required=True,
                                 help='password is required and must be a string')

        user_parser = reqparse.RequestParser()
        user_parser.add_argument('email', default=None)
        user_parser.add_argument('first_name', default=None)
        user_parser.add_argument('last_name', default=None)
        user_parser.add_argument('phone_number', default=None)
        user_parser.add_argument('address', default=None)
        user_parser.add_argument('date_of_birth', type=str, default=None)

        args = auth_parser.parse_args()
        optional_args = user_parser.parse_args()

        username = args['username']
        password = args['password']

        try:
            user = User.query.filter_by(username=username).first()
            if user:
                return {'error': 'User already exists'}, 400

            hashed_password = bcrypt.hashpw(
                password.encode('utf-8'), bcrypt.gensalt())

            date_of_birth_str = optional_args['date_of_birth']
            date_of_birth = None
            if date_of_birth_str:
                date_of_birth = datetime.strptime(
                    date_of_birth_str, '$Y-$M-$d')

            new_user = User(
                username=username,
                email=optional_args['email'],
                password=hashed_password,
                first_name=optional_args['first_name'],
                last_name=optional_args['last_name'],
                phone_number=optional_args['phone_number'],
                address=optional_args['address'],
                date_of_birth=date_of_birth
            )

            new_user.insert()

            return jsonify({
                "success": True,
                "message": "User created successfully"
            })
        except Exception as e:
            return {'error': 'User registration failed', 'message': str(e)}, 500


class Login(Resource):
    def post(self):
        auth_parser = reqparse.RequestParser()
        auth_parser.add_argument(
            'username', type=str, required=True, help="Username is required and must be a string")
        auth_parser.add_argument(
            'password', required=True, help='Password is required and must be a string')

        args = auth_parser.parse_args()
        username = args['username']
        password = args['password']

        try:
            user = User.query.filter_by(username=username).first()
            if not user or not bcrypt.checkpw(password.encode('utf-8'), user.password.encode('utf-8')):
                return {'message': 'Invalid username or password'}, 401

            access_token = create_access_token(identity=username)
            return {'token': access_token}
        except Exception as e:
            return {'error': 'Login failed', 'message': str(e)}, 500


class UserProfile(Resource):
    @jwt_required()
    def get(self):
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()

        user_data = {
            "id": user.id,
            "username": user.username,
            "email": user.email,
            "first_name": user.first_name,
            "last_name": user.last_name,
            "phone_number": user.phone_number,
            "address": user.address,
            "balance": user.balance,
            "date_of_birth": user.date_of_birth
        }

        return jsonify(user_data)

    @jwt_required()
    def put(self):
        current_user = get_jwt_identity()
        user_parser = reqparse.RequestParser()
        user_parser.add_argument('email', default=None)
        user_parser.add_argument('first_name', default=None)
        user_parser.add_argument('last_name', default=None)
        user_parser.add_argument('phone_number', default=None)
        user_parser.add_argument('address', default=None)
        user_parser.add_argument('date_of_birth', type=str, default=None)

        args = user_parser.parse_args()
        user = User.query.filter_by(username=current_user).first_or_404()

        if current_user != user.username:
            abort(403, description='Unauthorized')

        if 'username' in args or 'password' in args:
            abort(400, description='can\'t update username or password')

        if not any(args.values()):
            abort(400, description="Please provide fields to update")

        try:
            if args['date_of_birth']:
                date_of_birth_str = args['date_of_birth']
                date_of_birth = datetime.strptime(
                    date_of_birth_str, '%Y-%m-%d')
                user.date_of_birth = date_of_birth

            if args['email']:
                user.email = args['email']
            if args['first_name']:
                user.first_name = args['first_name']
            if args['last_name']:
                user.last_name = args['last_name']
            if args['phone_number']:
                user.phone_number = args['phone_number']
            if args['address']:
                user.address = args['address']

            user.update()

            return jsonify({
                'message': 'User profile updated successfully'
            })
        except ValueError as ve:
            return {'error': 'User profile update failed', 'message': str(ve)}, 400
        except Exception as e:
            return {'error': 'User profile update failed', 'message': str(e)}, 500

    @jwt_required()
    def delete(self):
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()

        loan = User.query.filter_by(user_id=user).first_or_404()

        if current_user != user.username:
            abort(403, description='Unauthorized')

        if loan:
            return jsonify({'message': 'You have to pay your loan before you can delete account'}), 400

        if user.balance > 0:
            return jsonify({'message': 'Please withdraw you balance before closing account'}), 400

        user.delete()

        return jsonify({"message": "User account deleted successfully"})


class LoanApplication(Resource):
    @jwt_required()
    def post(self):
        loan_parser = reqparse.RequestParser()
        loan_parser.add_argument('amount', required=True, type=float,
                                 help='Please provide the loan amount you want')
        loan_parser.add_argument('due_date', type=int, default=30)

        args = loan_parser.parse_args()
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()

        if not user.is_profile_complete():
            return {'message': 'Please complete you profile before you apply for a'}

        loan = Loan.query.filter_by(user_id=user.id).first()

        if loan and loan.status != 'paid':
            return {"message": "You already have an active loan."}, 400

        try:
            due_date_days = args['due_date']

            due_date = validate_due_date(due_date_days)
            loan = Loan(
                amount=args['amount'],
                user_id=user.id,
                due_date=due_date
            )
            loan.insert()
        except ValueError as e:
            return jsonify({'error': str(e)}), 400

        return jsonify({'message': 'Loan application submitted successfully'})


class LoansPending(Resource):
    @jwt_required()
    def get(self):
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()

        if user.type != 'admin' and user.type != 'financial_officer':
            print(user.type)
            return {'message': 'Unauthorized to access this endpoint'}, 403

        loans = Loan.query.filter((Loan.status == "pending") | (
            Loan.status == "approved")).all()
        if not loans:
            return {'message': 'There is no loan pending for approval or disbursement'}
        loan_data_list = []
        for loan in loans:
            interest = loan.amount - (loan.amount * loan.interest_rate)
            loan_data = {
                'loan_id': loan.id,
                'loan_amount': loan.amount,
                'loan_status': loan.status,
                'interest': interest,
                'Loan_date': loan.timestamp,
            }
            loan_data_list.append(loan_data)

        return jsonify({'loans': loan_data_list})


class LoanApproval(Resource):
    @jwt_required()
    def put(self, loan_id):
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()
        loan = Loan.query.get(loan_id)

        if not loan:
            return {"message": "Loan not found"}, 404

        if user.type != 'admin':
            return {"message": "Unauthorized to approve loans"}, 403

        if loan.status == 'pending':
            loan.status = 'approved'
            loan.approved_by = user.id
            loan.update()

            return {'message': 'Loan approved successfully'}


class LoanDisbursement(Resource):
    @jwt_required()
    def put(self, loan_id):
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()
        loan = Loan.query.get(loan_id)

        if not loan:
            return {'message': 'Loan not found'}, 404

        if user.type != 'financial_officer':
            return {'message': 'Unauthorized to disburse loans'}, 403

        if loan.status == 'approved':
            loan.status = 'disbursed'
            loan.disbursed_by = user.id
            user.balance += loan.amount
            loan.update()
            user.update()

            return {'message': 'Loan disbursed successfully'}


class LoanRepayment(Resource):
    @jwt_required()
    def post(self, loan_id):
        loan_repay = reqparse.RequestParser()
        loan_repay.add_argument(
            'type', type=validate_loan_repay, default='FULL')

        args = loan_repay.parse_args()
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()
        loan = Loan.query.get(loan_id)

        if not loan:
            return {'message': 'Loan not found'}, 404

        if loan.user_id != user.id:
            return {'message': 'Unauthorized to repay this loan'}, 403

        if loan.status != 'disbursed':
            return {'message': 'Loan must be disbursed to make a repayment'}, 400

        repay_type = args['type']
        if repay_type == 'FULL':
            amount = loan.remaining_balance
        elif repay_type == 'PARTIAL':
            amount = loan.remaining_balance / 2
        else:
            return {'message': 'Invalid repay type entered'}, 400

        if user.balance < amount:
            return {'message': 'Your balance is insufficient to make repayment'}, 400

        try:
            loan.remaining_balance -= amount
            user.balance -= amount
            repayment = Repayment(
                loan_id=loan.id,
                amount=amount,
                user_id=user.id
            )
            repayment.insert()

            if loan.remaining_balance == 0:
                loan.status = 'paid'
                loan.update()
            return {
                'message': 'Loan repayment successful'
            }

        except Exception as e:
            return {'error': 'Loan repayment failed', 'message': str(e)}, 500


class LoanHistory(Resource):
    @jwt_required()
    def get(self):
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()

        loans_history = db.session.query(Loan, Repayment).join(
            Repayment).filter(Loan.user_id == user.id).all()
        print(loans_history)

        loan_history_data = []
        for loan, repayment in loans_history:
            interest = loan.amount - (loan.amount * loan.interest_rate)
            loan_data = {
                'loan_id': loan.id,
                'loan_amount': loan.amount,
                'loan_status': loan.status,
                'interest': interest,
                'Loan_date': loan.timestamp,
                'repayment_amount': repayment.amount,
                'repayment_date': repayment.timestamp
            }

            loan_history_data.append(loan_data)

        return jsonify({'loan_history': loan_history_data})


class SendMoney(Resource):
    @jwt_required()
    def post(self):
        transaction_parser = reqparse.RequestParser()
        transaction_parser.add_argument('amount', type=float, required=True,
                                        help='Damn! provide the amount you want to send you dumb head')
        transaction_parser.add_argument(
            'receiver', type=str, required=True, help='do i have to tell you to provide the reciever?')
        transaction_parser.add_argument('description', type=str, default=None)

        args = transaction_parser.parse_args()
        receiver_username = args['receiver']
        transaction_amount = args['amount']
        description = args['description']
        current_user = get_jwt_identity()
        sender = User.query.filter_by(username=current_user).first_or_404()
        receiver = User.query.filter_by(
            username=receiver_username).first_or_404()

        if sender.balance < transaction_amount:
            return {'message': 'Don\'t be a thief, trying to send money you don\'t have'}, 400

        try:
            transaction_id = uuid4()
            sender.balance -= transaction_amount
            sender_transaction = Transaction(
                id=transaction_id,
                sender=sender.id,
                receiver=receiver.id,
                amount=transaction_amount,
                status='success',
                description=description,
                type='debit'
            )
            sender_transaction.insert()
            sender.update()

            receiver.balance += transaction_amount
            receiver_transaction = Transaction(
                id=transaction_id,
                sender=sender.id,
                receiver=receiver.id,
                amount=transaction_amount,
                status='success',
                description=description,
                type='credit'
            )
            receiver_transaction.insert()
            receiver.update()

            return {'message': 'Money sent successfully'}
        except Exception as e:
            return {'error': 'Transaction failed', 'message': str(e)}, 400


class TransactionnHistory(Resource):
    @jwt_required()
    def get(self):
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()

        transactions = Transaction.query.filter(
            (Transaction.sender_id == user.id) | (
                Transaction.receiver_id == user.id)
        ).all()
        transaction_history = []
        for transaction in transactions:
            transaction_data = {
                'id': transaction.id,
                'sender': transaction.sender.username,
                'receiver': transaction.receiver.username,
                'amount': transaction.amount,
                'status': transaction.status,
                'timestamp': transaction.timestamp
            }
            transaction_history.append(transaction_data)
        return jsonify({'transactions': transaction_history})


class UserBalance(Resource):
    @jwt_required()
    def get(self):
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()
        return {'balance': user.balance}


api.add_resource(Register, '/users/register')
api.add_resource(Login, '/users/login')
api.add_resource(UserProfile, '/users/profile')
api.add_resource(LoanApplication, '/loans/application')
api.add_resource(LoansPending, '/loans/applications/pending')
api.add_resource(
    LoanApproval, '/loans/approval/<int:loan_id>')
api.add_resource(LoanDisbursement, '/loans/disbursement/<int:loan_id>')
api.add_resource(LoanRepayment, '/loans/repayment/<int:loan_id>')
api.add_resource(LoanHistory, '/loans/history')
api.add_resource(SendMoney, '/transactions/send')
api.add_resource(TransactionnHistory, '/transactions/history')
api.add_resource(UserBalance, '/users/profile/balance')


if __name__ == "__main__":
    app = create_app("development")
    app.run(host="0.0.0.0", port=5000)
