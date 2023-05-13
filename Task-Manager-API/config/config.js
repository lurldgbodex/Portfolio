const mongoose = require('mongoose');
const host = process.env.DB_HOST || 'localhost';
const port = process.env.DB_PORT || 27017;
const db = process.env.DB || 'task_manager';
const jwt_key = process.env.JWT_KEY || 'secret-key';
const MONGODB_URI= `mongodb://${host}:${port}/${db}`

const db_connect = async () => {
    try {
        await mongoose.connect(MONGODB_URI)
        console.log('Database connected...')
    } catch (error) {
        console.log(error.message);
        process.exit(1)
    }
}

module.exports = {
    JWT_SECRET: `${jwt_key}`,
    db_connect
};