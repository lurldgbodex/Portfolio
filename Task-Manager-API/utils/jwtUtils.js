const jwt = require('jsonwebtoken');
const { JWT_SECRET } = require('../config/config');

// Generate JWT token
const generateToken = (userId) => {
  const payload = {
    userId: userId.toString(),
  };
  return jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' });
};

module.exports = generateToken;
