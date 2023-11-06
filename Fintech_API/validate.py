from enum import Enum
from datetime import datetime, timedelta


class LoanValidation(Enum):
    PENDING = 'Pending'
    APPROVED = 'Approved'
    REJECTED = 'Rejected'
    PAID = 'Paid'

    def validate_loan_status(self, value):
        if value not in LoanValidation.__members__:
            raise ValueError(f"Invalid loan status: {value}")
        return value

    def validate_due_date(self, value):
        try:
            print(value)
            days = int(value)
            due_date = datetime.now() + timedelta(days=days)
            return due_date
        except ValueError:
            raise ValueError(
                "Invalid due date value. Please provide a valid number of days")
