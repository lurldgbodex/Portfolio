const chai = require('chai');
const chaiHttp = require('chai-http');
const { describe, it, beforeEach } = require('mocha');
const bcrypt = require('bcryptjs');
const app = require('../server');
const User = require('../models/User');

chai.use(chaiHttp);
const { expect } = chai;

describe('Authentication', () => {
  describe('Registration', () => {
    beforeEach(async () => {
      // Clear the user collection before each test
      await User.deleteMany();
    });

    it('should register a new user', async () => {
      const res = await chai
        .request(app)
        .post('/api/users/register')
        .send({
          name: 'John Doe',
          email: 'john@example.com',
          password: 'password123',
        });

      expect(res).to.have.status(200);
      expect(res.body).to.have.property('message').equal('User registered successfully');
    });

    it('should return an error when name is missing', async () => {
      const res = await chai
        .request(app)
        .post('/api/users/register')
        .send({
          email: 'john@example.com',
          password: 'password123',
        });

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Name is required');
    });

    it('should return an error when email is missing', async () => {
      const res = await chai
        .request(app)
        .post('/api/users/register')
        .send({
          name: 'John Doe',
          password: 'password123',
        });

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Email is required');
    });

    it('should return an error when password is missing', async () => {
      const res = await chai
        .request(app)
        .post('/api/users/register')
        .send({
          name: 'John Doe',
          email: 'john@example.com',
        });

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Password is required');
    });

    it('should return an error when email is already registered', async () => {
      // Create a user with the same email
      const existingUser = new User({
        name: 'Jane Smith',
        email: 'john@example.com',
        password: 'password456',
      });
      await existingUser.save();

      const res = await chai
        .request(app)
        .post('/api/users/register')
        .send({
          name: 'John Doe',
          email: 'john@example.com',
          password: 'password123',
        });

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Email is already registered');
    });
  });

  describe('User Login', () => {
    beforeEach(async () => {
      // Clear the user collection before each test
      await User.deleteMany();

      // Create a user for testing
      const hashedPassword = await bcrypt.hash('password123', 10);
      const user = new User({
        name: 'John Doe',
        email: 'john@example.com',
        password: hashedPassword,
      });
      await user.save();
    });

    it('should login a user with valid credentials', async () => {
      const res = await chai
        .request(app)
        .post('/api/users/login')
        .send({
          email: 'john@example.com',
          password: 'password123',
        });

      expect(res).to.have.status(200);
      expect(res.body).to.have.property('token');
    });

    it('should return an error with invalid email', async () => {
      const res = await chai
        .request(app)
        .post('/api/users/login')
        .send({
          email: 'invalid@example.com',
          password: 'password123',
        });

      expect(res).to.have.status(401);
      expect(res.body).to.have.property('error').equal('Invalid credentials');
    });

    it('should return an error with invalid password', async () => {
      const res = await chai
        .request(app)
        .post('/api/users/login')
        .send({
          email: 'john@example.com',
          password: 'invalidpassword',
        });

      expect(res).to.have.status(401);
      expect(res.body).to.have.property('error').equal('Invalid credentials');
    });

    it('should return an error with missing email', async () => {
      const res = await chai
        .request(app)
        .post('/api/users/login')
        .send({
          password: 'password123',
        });

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Email and password are required');
    });

    it('should return an error with missing password', async () => {
      const res = await chai
        .request(app)
        .post('/api/users/login')
        .send({
          email: 'john@example.com',
        });

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Email and password are required');
    });
  });
});
