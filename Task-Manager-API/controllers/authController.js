const bcrypt = require('bcryptjs');
const User = require('../models/User');
const generateToken = require('../utils/jwtUtils');
const errorHandler = require('../utils/errorHandler');

// handle new user registration
const registerUser = async (req, res, next) => {
  try {
    const { name, email, password } = req.body;
    if (!name) {
      return res.status(400).json({ error: 'Name is required' });
    }
    if (!email) {
      return res.status(400).json({ error: 'Email is required' });
    }
    if (!password) {
      return res.status(400).json({ error: 'Password is required' });
    }

    // Check if user already exists
    const existingUser = await User.exists({ email });
    if (existingUser) {
      return res.status(400).json({ error: 'Email is already registered' });
    }

    // Create new user with encrypted password
    const hashedPassword = await bcrypt.hash(password, 10);
    const newUser = new User({
      name,
      email,
      password: hashedPassword,
    });
    await newUser.save();
    return res.json({ message: 'User registered successfully' });
  } catch (error) {
    next(error);
  }
};

// handle user login
const userLogin = async (req, res) => {
  try {
    const { email, password } = req.body;

    // Validate data
    if (!email || !password) {
      return res.status(400).json({ error: 'Email and password are required' });
    }

    // Find user from the database
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }

    // Check password match
    const passwordMatch = await bcrypt.compare(password, user.password);
    if (!passwordMatch) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }

    // Generate a new token for the user
    const token = generateToken(user._id);
    return res.json({ token });
  } catch (error) {
    // Use centralized error handling middleware instead
    errorHandler(error, res);
  }
};

module.exports = { registerUser, userLogin };
