import os
from dotenv import load_dotenv

# Load environment variables from the .env file
load_dotenv()


class Config:
    db_user = os.getenv('DB_USER')
    db_password = os.getenv('DB_PASSWORD')
    db_name = os.getenv('DB_NAME')
    db_host = os.getenv('DB_HOST')
    secret_key = os.getenv('SECRET_KEY')

    database_url = f'mysql+mysqlconnector://{db_user}:{db_password}@{db_host}/{db_name}'

    SQLALCHEMY_DATABASE_URI = database_url
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    JWT_SECRET_KEY = secret_key


class DevelopmentConfig(Config):
    DEBUG = True


class ProductionConfig(Config):
    DEBUG = False


config_by_name = {
    "development": DevelopmentConfig,
    "production": ProductionConfig
}
