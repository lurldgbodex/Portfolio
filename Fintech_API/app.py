from flask import Flask, jsonify, abort
from flask_restful import Resource, Api, reqparse
from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity
import bcrypt
from models import db, User, Loan
from config import config_by_name
from validate import LoanValidation

app = Flask(__name__)
api = Api(app)
jwt = JWTManager(app)

auth_parser = reqparse.RequestParser()

auth_parser.add_argument('username', type=str, required=True,
                         help="username is required and must be a string")
auth_parser.add_argument('password', required=True,
                         help='password is required and must be a string')

common_parser = reqparse.RequestParser()
common_parser.add_argument('first_name', default=None)
common_parser.add_argument('email', default=None)
common_parser.add_argument('last_name', default=None)
common_parser.add_argument('phone_number', default=None)
common_parser.add_argument('address', default=None)
common_parser.add_argument('date_of_birth', default=None)

loan_parser = reqparse.RequestParser()
loan_parser.add_argument('amount', required=True, type=int,
                         help='Please provide the loan amount you want')
loan_parser.add_argument('due_date', type=LoanValidation.validate_due_date, required=True,
                         help='Invalid due date value')
loan_parser.add_argument('status', type=LoanValidation.validate_loan_status)


def create_app(config_name):
    app.config.from_object(config_by_name[config_name])

    with app.app_context():
        db.init_app(app)
        db.create_all()

    return app


class Register(Resource):
    def post(self):
        args = auth_parser.parse_args()
        optional_args = common_parser.parse_args()
        username = args['username']
        password = args['password']

        user = User.query.filter_by(username=username).first()
        if user:
            return {'error': 'User already exists'}, 400

        hashed_password = bcrypt.hashpw(
            password.encode('utf-8'), bcrypt.gensalt())
        new_user = User(
            username=username,
            email=optional_args['email'],
            password=hashed_password,
            first_name=optional_args['first_name'],
            last_name=optional_args['last_name'],
            phone_number=optional_args['phone_number'],
            address=optional_args['address'],
            date_of_birth=optional_args['date_of_birth']
        )
        new_user.insert()

        return jsonify({
            "success": True,
            "message": "User created successfully"
        })


class Login(Resource):
    def post(self):
        args = auth_parser.parse_args()
        username = args['username']
        password = args['password']

        user = User.query.filter_by(username=username).first()
        if not user or not bcrypt.checkpw(password.encode('utf-8'), user.password.encode('utf-8')):
            return {'message': 'Invalid username or password'}, 401

        access_token = create_access_token(identity=username)
        return {'token': access_token}


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

        return user_data

    @jwt_required()
    def put(self):
        current_user = get_jwt_identity()
        args = common_parser.parse_args()
        user = User.query.filter_by(username=current_user).first_or_404()

        if current_user != user.username:
            abort(403, description='Unauthorized')

        if 'username' in args or 'password' in args:
            abort(400, description='can\'t update username or password')

        if not any(args.values()):
            abort(400, description="Please provide fields to update")

        user.email = args['email']
        user.first_name = args['first_name']
        user.last_name = args['last_name']
        user.phone_number = args['phone_number']
        user.address = args['address']

        user.update()

        return jsonify({
            'message': 'User profile updated successfully'
        })

    @jwt_required()
    def delete(self):
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()

        if current_user != user.username:
            abort(403, description='Unauthorized')

        user.delete()

        return jsonify({"message": "User account deleted successfully"})


class LoanApplication(Resource):
    @jwt_required()
    def post(self):
        args = loan_parser.parse_args()
        current_user = get_jwt_identity()
        user = User.query.filter_by(username=current_user).first_or_404()

        loan = Loan.query.filter_by(user_id=user.id).first()

        if loan and loan.status == 'approved':
            return jsonify({"message": "You already have an active loan."}), 400

        try:
            due_date_days = args['due_date']
            due_date = LoanValidation.validate_due_date(due_date_days)
            loan = Loan(
                amount=args['amount'],
                user_id=user.id,
                due_date=due_date
            )
            loan.insert()
        except ValueError as e:
            return jsonify({'error': str(e)}), 400

        return jsonify({'message': 'Loan application submitted successfully'})


api.add_resource(Register, '/users/register')
api.add_resource(Login, '/users/login')
api.add_resource(UserProfile, '/users/profile')
api.add_resource(LoanApplication, '/loans/application')

if __name__ == "__main__":
    app = create_app("development")
    app.run(host="0.0.0.0", port=5000)
