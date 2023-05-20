const chai = require('chai');
const chaiHttp = require('chai-http');
const sinon = require('sinon');
const {
  describe, it, beforeEach, afterEach,
} = require('mocha');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const { ObjectId } = require('mongodb');
const Task = require('../models/Task');
const User = require('../models/User');
const app = require('../server');

chai.use(chaiHttp);
const { expect } = chai;

describe('Tasks', () => {
  let token;
  let taskId;
  let taskData;
  let user;

  beforeEach(async () => {
    await User.deleteMany();
    await Task.deleteMany();

    const userData = {
      name: 'testuser',
      email: 'testuser@mail.com',
      password: await bcrypt.hash('testpassword', 10),
    };
    user = await User.create(userData);

    const payload = {
      email: userData.email,
      password: 'testpassword',
    };
    const res = await chai.request(app)
      .post('/api/users/login')
      .send(payload);
    token = res.body.token;

    // Create a task
    taskData = {
      title: 'Task 1',
      description: 'Description for Task 1',
      userId: user._id,
    };
    const task = await Task.create(taskData);
    taskId = task._id.toString();
  });

  afterEach(async () => {
    sinon.restore();
  });

  describe('POST /tasks', () => {
    beforeEach(() => {
      sinon.stub(Task.prototype, 'save').resolves();
    });

    it('should create a new task', async () => {
      const taskData = {
        title: 'Task 1',
        description: 'Description for Task 1',
      };

      const res = await chai
        .request(app)
        .post('/api/tasks')
        .set('Authorization', `Bearer ${token}`)
        .send(taskData);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property('message').equal('Task created successfully');
      expect(res.body).to.have.property('task');
      expect(res.body.task).to.have.property('title').equal(taskData.title);
      expect(res.body.task).to.have.property('description').equal(taskData.description);
    });

    it('should return an error when title is missing', async () => {
      const taskData = {
        description: 'Description for Task 1',
      };

      const res = await chai
        .request(app)
        .post('/api/tasks')
        .set('Authorization', `Bearer ${token}`)
        .send(taskData);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Title is required');
    });

    it('should return an error when description is missing', async () => {
      const taskData = {
        title: 'Task 1',
      };

      const res = await chai
        .request(app)
        .post('/api/tasks')
        .set('Authorization', `Bearer ${token}`)
        .send(taskData);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Description is required');
    });

    it('should return an error when title exceeds the maximum length', async () => {
      const taskData = {
        title: 'This is a very long title that exceeds the maximum length allowed for a task title',
        description: 'Description for Task 1',
      };

      const res = await chai
        .request(app)
        .post('/api/tasks')
        .set('Authorization', `Bearer ${token}`)
        .send(taskData);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Title length exceeds the maximum limit');
    });

    it('should return an error when the request payload contains additional properties', async () => {
      const taskData = {
        title: 'Task 1',
        description: 'Description for Task 1',
        additionalProperty: 'Additional property',
      };

      const res = await chai
        .request(app)
        .post('/api/tasks')
        .set('Authorization', `Bearer ${token}`)
        .send(taskData);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Invalid request payload');
    });

    it('should return an error when an invalid user tries to create a task', async () => {
      const taskData = {
        title: 'Task 1',
        description: 'Description for Task 1',
      };

      const invalidToken = jwt.sign({ email: 'invalid@mail.com' }, 'secret_key', { expiresIn: '1h' });

      const res = await chai
        .request(app)
        .post('/api/tasks')
        .set('Authorization', `Bearer ${invalidToken}`)
        .send(taskData);

      expect(res).to.have.status(403);
      expect(res.body).to.have.property('error').equal('Invalid Token');
    });

    it('should return an error when an unauthorized user tries to create a task', async () => {
      const taskData = {
        title: 'Task 1',
        description: 'Description for Task 1',
      };

      const res = await chai
        .request(app)
        .post('/api/tasks')
        .send(taskData);

      expect(res).to.have.status(401);
      expect(res.body).to.have.property('error').equal('unauthorized');
    });
  });

  describe('GET /tasks', () => {
    it('should retrieve tasks associated with the authenticated user', async () => {
      // Create a user and tasks associated with the user
      const user1 = await User.create({
        name: 'User 1',
        email: 'user1@example.com',
        password: await bcrypt.hash('password1', 10),
      });

      const user2 = await User.create({
        name: 'User 2',
        email: 'user2@example.com',
        password: await bcrypt.hash('password2', 10),
      });

      const task1 = await Task.create({
        title: 'Task 2',
        description: 'Description for Task 2',
        userId: user1._id,
      });

      const task2 = await Task.create({
        title: 'Task 3',
        description: 'Description for Task 3',
        userId: user2._id,
      });

      const task3 = await Task.create({
        title: 'Task 4',
        description: 'Task-4 description',
        userId: user._id,
      });

      const res = await chai
        .request(app)
        .get('/api/tasks')
        .set('Authorization', `Bearer ${token}`);

      expect(res).to.have.status(200);
      expect(res.body).to.be.an('array');
      expect(res.body).to.have.lengthOf(2);
      expect(res.body[0]).to.have.property('title', taskData.title);
      expect(res.body[0]).to.have.property('description', taskData.description);
      expect(res.body[0]).to.have.property('userId', user._id.toString());
      expect(res.body[1]).to.have.property('title', task3.title);
      expect(res.body[1]).to.have.property('description', task3.description);
      expect(res.body[1]).to.have.property('userId', task3.userId.toString());
    });

    it('should retrieve a task by its ID', async () => {
      const res = await chai.request(app)
        .get(`/api/tasks/${taskId}`)
        .set('Authorization', `Bearer ${token}`);

      expect(res).to.have.status(200);
      expect(res.body).to.be.an('object');
      expect(res.body).to.have.property('title', 'Task 1');
      expect(res.body).to.have.property('description', 'Description for Task 1');
      expect(res.body).to.have.property('userId', user._id.toString());
    });

    it('should return an error when the task ID is invalid', async () => {
      const invalidTaskId = 'invalid-task-id';

      const res = await chai.request(app)
        .get(`/api/tasks/${invalidTaskId}`)
        .set('Authorization', `Bearer ${token}`);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Invalid task ID');
    });

    it('should return an error when the task ID does not exist', async () => {
      const nonExistentTaskId = new ObjectId().toString();

      const res = await chai.request(app)
        .get(`/api/tasks/${nonExistentTaskId}`)
        .set('Authorization', `Bearer ${token}`);

      expect(res).to.have.status(404);
      expect(res.body).to.have.property('error').equal('Task not found');
    });
  });

  describe('PATCH /tasks/:taskId', () => {
    it('should update a task', async () => {
      const updatedTaskData = {
        title: 'Updated Task',
        description: 'Updated description',
        status: 'IN_PROGRESS',
      };

      const res = await chai.request(app)
        .patch(`/api/tasks/${taskId}`)
        .set('Authorization', `Bearer ${token}`)
        .send(updatedTaskData);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property('message').equal('Task updated successfully');
      expect(res.body).to.have.property('task');
      expect(res.body.task).to.have.property('title').equal(updatedTaskData.title);
      expect(res.body.task).to.have.property('description').equal(updatedTaskData.description);
      expect(res.body.task).to.have.property('status').equal(updatedTaskData.status);
    });

    it('should update the title of a task', async () => {
      const updatedTaskData = {
        title: 'Updated Title',
      };

      const res = await chai.request(app)
        .patch(`/api/tasks/${taskId}`)
        .set('Authorization', `Bearer ${token}`)
        .send(updatedTaskData);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property('message').equal('Task updated successfully');
      expect(res.body).to.have.property('task');
      expect(res.body.task).to.have.property('title').equal(updatedTaskData.title);
      expect(res.body.task).to.have.property('description').equal('Description for Task 1');
      expect(res.body.task).to.have.property('status').equal('TODO');
    });

    it('should update the description of a task', async () => {
      const updatedTaskData = {
        description: 'Updated Description',
      };

      const res = await chai.request(app)
        .patch(`/api/tasks/${taskId}`)
        .set('Authorization', `Bearer ${token}`)
        .send(updatedTaskData);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property('message').equal('Task updated successfully');
      expect(res.body).to.have.property('task');
      expect(res.body.task).to.have.property('title').equal('Task 1');
      expect(res.body.task).to.have.property('description').equal(updatedTaskData.description);
      expect(res.body.task).to.have.property('status').equal('TODO');
    });

    it('should update the status of a task', async () => {
      const updatedTaskData = {
        status: 'DONE',
      };

      const res = await chai.request(app)
        .patch(`/api/tasks/${taskId}`)
        .set('Authorization', `Bearer ${token}`)
        .send(updatedTaskData);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property('message').equal('Task updated successfully');
      expect(res.body).to.have.property('task');
      expect(res.body.task).to.have.property('title').equal('Task 1');
      expect(res.body.task).to.have.property('description').equal('Description for Task 1');
      expect(res.body.task).to.have.property('status').equal(updatedTaskData.status);
    });

    it('should return an error for an invalid task ID', async () => {
      const invalidTaskId = 'invalid-task-id';

      const res = await chai.request(app)
        .patch(`/api/tasks/${invalidTaskId}`)
        .set('Authorization', `Bearer ${token}`)
        .send({});

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('Invalid task ID');
    });

    it('should return an error for a non-existing task ID', async () => {
      const nonExistingTaskId = '60a0f2f4c9e1c82848e8b7b2';
      const updatedTaskData = {
        status: 'iN_PROGRESS',
      };

      const res = await chai.request(app)
        .patch(`/api/tasks/${nonExistingTaskId}`)
        .set('Authorization', `Bearer ${token}`)
        .send(updatedTaskData);

      expect(res).to.have.status(404);
      expect(res.body).to.have.property('error').equal('Task not found');
    });

    it('should return an error if no update fields are provided', async () => {
      const res = await chai.request(app)
        .patch(`/api/tasks/${taskId}`)
        .set('Authorization', `Bearer ${token}`)
        .send({});

      expect(res).to.have.status(400);
      expect(res.body).to.have.property('error').equal('No fields to update');
    });
  });

  describe('DELETE /tasks/:taskId', () => {
    beforeEach(() => {
      sinon.stub(Task, 'findByIdAndDelete').resolves({ _id: '1234567890' });
    });

    it('should delete a task and return a success message', async () => {
      const taskId = '1234567890';
      const res = await chai
        .request(app)
        .delete(`/api/tasks/${taskId}`)
        .set('Authorization', `Bearer ${token}`);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property('message').equal('Task deleted successfully');
    });

    it('should return an error when task is not found', async () => {
      const taskId = '1234567890';
      Task.findByIdAndDelete.resolves(null); // Simulate task not found

      const res = await chai
        .request(app)
        .delete(`/api/tasks/${taskId}`)
        .set('Authorization', `Bearer ${token}`);

      expect(res).to.have.status(404);
      expect(res.body).to.have.property('error').equal('Task not found');
    });
  });
});
