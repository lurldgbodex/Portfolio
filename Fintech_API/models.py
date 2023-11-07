from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()


class DbMethods:
    def insert(self):
        db.session.add(self)
        db.session.commit()

    def update(self):
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()


# Define the User model
class User(db.Model, DbMethods):
    __tablename__ = 'users'

    id = db.Column(db.Integer, primary_key=True)
    first_name = db.Column(db.String(100), nullable=True)
    last_name = db.Column(db.String(100), nullable=True)
    username = db.Column(db.String(100), unique=True, nullable=False)
    password = db.Column(db.String(100), nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=True)
    phone_number = db.Column(db.String(100), unique=True, nullable=True)
    address = db.Column(db.String(255), nullable=True)
    date_of_birth = db.Column(db.DateTime, nullable=True)
    balance = db.Column(db.Float, default=0.0)
    type = db.Column(db.Enum('admin', 'financial_officer',
                                      'user',), default='user')
    loans = db.relationship(
        'Loan', backref='user', lazy=True)
    transactions = db.relationship(
        'Transaction', primaryjoin='or_(User.id == Transaction.sender_id, User.id == Transaction.receiver_id)', backref='user', lazy=True)
    payments = db.relationship(
        'Payment', backref='user', lazy=True)
    repayments = db.relationship('Repayment', backref='user', lazy=True)

    def __init__(self, username, password, email=None, first_name=None, last_name=None, phone_number=None, address=None, date_of_birth=None):
        self.username = username
        self.password = password
        self.email = email
        self.first_name = first_name
        self.last_name = last_name
        self.phone_number = phone_number
        self.address = address
        self.date_of_birth = date_of_birth

    def is_profile_complete(self):
        optional_fields = ['email', 'first_name',
                           'last_name', 'phone_number', 'address']

        for field in optional_fields:
            if not getattr(self, field):
                return False
        return True


# Define the Loan model
class Loan(db.Model, DbMethods):
    __tablename__ = 'loans'

    id = db.Column(db.Integer, primary_key=True)
    amount = db.Column(db.Float, nullable=False)
    interest_rate = db.Column(db.Float, nullable=True, default=0.1)
    status = db.Column(db.Enum('pending', 'approved',
                       'rejected', 'disbursed', 'paid'), default='pending')
    due_date = db.Column(db.DateTime, nullable=False)
    remaining_balance = db.Column(db.Float, nullable=False)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    repayments = db.relationship('Repayment', backref='loan', lazy=True)

    def __init__(self, amount, due_date, user_id, interest_rate=0.1):
        self.amount = amount
        self.interest_rate = interest_rate
        self.remaining_balance = amount + (amount * interest_rate)
        self.due_date = due_date
        self.user_id = user_id


class Repayment(db.Model, DbMethods):
    __tablename__ = 'repayments'

    id = db.Column(db.Integer, primary_key=True)
    loan_id = db.Column(db.Integer, db.ForeignKey('loans.id'), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    amount = db.Column(db.Float, nullable=False)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)

    def __init__(self, loan_id, user_id, amount):
        self.loan_id = loan_id
        self.user_id = user_id
        self.amount = amount


# Define the Transaction model
class Transaction(db.Model, DbMethods):
    __tablename__ = 'transactions'

    id = db.Column(db.Integer, primary_key=True)
    transaction_type = db.Column(
        db.Enum('Deposit', 'Withdrawal', 'Transfer'), nullable=False)
    amount = db.Column(db.Float, nullable=False)
    description = db.Column(db.String(100), nullable=True)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)
    sender_id = db.Column(
        db.Integer, db.ForeignKey('users.id'), nullable=False)
    receiver_id = db.Column(
        db.Integer, db.ForeignKey('users.id'), nullable=False)

    def __init__(self, amount, sender, receiver, type, description=None):
        self.amount = amount
        self.transaction_type = type
        self.sender_id = sender
        self.receiver_id = receiver
        self.description = description

# Define the Payment model


class Payment(db.Model, DbMethods):
    __tablename__ = 'payments'

    id = db.Column(db.Integer, primary_key=True)
    payee_name = db.Column(db.Integer, nullable=False)
    payment_type = db.Column(db.Enum('Online', 'Utility'), nullable=False)
    amount = db.Column(db.Float, nullable=False)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)

    def __init__(self, amount, payee, type):
        self.amount = amount
        self.payee_name = payee
        self.payment_type = type
