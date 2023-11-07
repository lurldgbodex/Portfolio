from enum import Enum
from datetime import datetime, timedelta


class LoanRepay(Enum):
    FULL = 'full'
    PARTIAL = 'partial'


def validate_loan_repay(value):
    value = value.strip()
    if value not in LoanRepay.__members__:
        print(LoanRepay.__members__)
        raise ValueError(f"Invalid loan repay type: {value}")
    return value


def validate_due_date(value):
    try:
        print(value)
        days = int(value)
        due_date = datetime.now() + timedelta(days=days)
        return due_date
    except ValueError:
        raise ValueError(
            "Invalid due date value. Please provide a valid number of days")
