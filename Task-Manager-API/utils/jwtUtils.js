const jwt = require('jsonwebtoken');
const { JWT_SECRET } = require('../config/config');

// Generate JWT token
const generateToken = (payload) => {
    return jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' });
}

//verify JWT Token
const verifyToken = (token) => {
    try {
        const decoded = jwt.verify(token, JWT_SECRET);
        return decoded;
    } catch (error) {
        throw new Error('Invalid token')
    }
};

module.exports = {
    generateToken,
    verifyToken
};