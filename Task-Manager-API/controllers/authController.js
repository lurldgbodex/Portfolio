const bcrypt = require('bcryptjs');
const User = require('../models/User');
const { generateToken, verifyToken } = require('../utils/jwtUtils');

// handle new user registration
const registerUser = async(req, res) => {
    try {
        const { name, email, password } = req.body;

        //check if user already exists
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ error: 'Email is already registered'})
        }

        //created new user with encrypt password
        const hashedPassword = await bcrypt.hash(password, 10)
        const newUser = new User({
            name,
            email,
            password: hashedPassword
        });
        await newUser.save();
        return res.json({ message: 'User registered successfully' });

    } catch (error) {
        return res.status(500).json({ error: 'Internal server error' });
    }
}

//handle user login
const userLogin = async(req, res) => {
    try {
        const { email, password} = req.body;

        //find user from database
        const user = await User.findOne({ email });
        if(!user) {
            return res.status(401).json({ error: 'Invalid email or password' })
        }

        //check password match
        const passwordMatch = await bcrypt.compare(password, user.password);
        if (!passwordMatch) {
            return res.status(401).json({ error: 'Invalid email or password' })
        }

        // Generate a new token for the user
        const token = generateToken({ userId: user._id })
        user.token = token;
        await user.save();
        return res.json({ token });
        
    } catch (error) {
        res.status(500).json({ error: 'Internal server error' })
    }
}

module.exports = { registerUser, userLogin }