const mongoose = require('mongoose');
const crypto = require('crypto');

const host = process.env.DB_HOST || 'localhost';
const port = process.env.DB_PORT || 27017;
const db = process.env.DB_NAME || 'task_manager';
const MONGODB_URI = `mongodb://${host}:${port}/${db}`;

const JWT_SECRET = process.env.JWT_SECRET || crypto.randomBytes(64).toString('hex');

const dbConnect = async () => {
  try {
    await mongoose.connect(MONGODB_URI);
    console.log('Database connected...');
  } catch (error) {
    console.log(error.message);
    process.exit(1);
  }
};

module.exports = {
  JWT_SECRET,
  dbConnect,
};
