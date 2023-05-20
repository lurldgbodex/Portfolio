const jwt = require('jsonwebtoken');
const mongoose = require('mongoose');
const Task = require('../models/Task');
const { JWT_SECRET } = require('../config/config');
const errorHandler = require('../utils/errorHandler');

const getUserIDFromRequest = (req) => {
  const token = req.headers.authorization.split('Bearer ')[1];
  if (!token) {
    throw new Error('No token provided');
  }

  try {
    const decodedToken = jwt.verify(token, JWT_SECRET);
    const { userId } = decodedToken;
    return userId;
  } catch (error) {
    throw new Error('Invalid token');
  }
};

const createTask = async (req, res) => {
  try {
    const { title, description, ...extraProps } = req.body;

    const userId = getUserIDFromRequest(req);

    if (!title) {
      return res.status(400).json({ error: 'Title is required' });
    }

    if (title.length > 50) {
      return res
        .status(400)
        .json({ error: 'Title length exceeds the maximum limit' });
    }

    if (!description) {
      return res.status(400).json({ error: 'Description is required' });
    }

    if (Object.keys(extraProps).length > 0) {
      return res.status(400).json({ error: 'Invalid request payload' });
    }

    const newTask = new Task({
      title,
      description,
      userId,
    });

    await newTask.save();

    return res.json({ message: 'Task created successfully', task: newTask });
  } catch (error) {
    errorHandler(error, res);
  }
};

const getAllTasks = async (req, res) => {
  try {
    const userId = getUserIDFromRequest(req);

    const tasks = await Task.find({ userId });

    if (tasks.length === 0) {
      return res.status(404).json({ error: 'No tasks found' });
    }

    return res.json(tasks);
  } catch (error) {
    errorHandler(error, res);
  }
};

const getTaskById = async (req, res) => {
  try {
    const { taskId } = req.params;

    // Validate if the task ID is a valid ObjectId
    if (!mongoose.isValidObjectId(taskId)) {
      return res.status(400).json({ error: 'Invalid task ID' });
    }

    const task = await Task.findById(taskId);
    if (!task) {
      return res.status(404).json({ error: 'Task not found' });
    }
    return res.json(task);
  } catch (error) {
    errorHandler(error, res);
  }
};

const updateTask = async (req, res) => {
  try {
    const { taskId } = req.params;
    const { title, description, status } = req.body;

    // Validate if task Id is a valid ObjectId
    if (!mongoose.isValidObjectId(taskId)) {
      return res.status(400).json({ error: 'Invalid task ID' });
    }

    if (!title && !description && !status) {
      return res.status(400).json({ error: 'No fields to update' });
    }

    const task = await Task.findById(taskId);
    if (!task) {
      return res.status(404).json({ error: 'Task not found' });
    }

    if (title) {
      task.title = title;
    }
    if (description) {
      task.description = description;
    }
    if (status) {
      task.status = status;
    }
    task.updatedAt = Date.now();

    await task.save();
    return res.json({ message: 'Task updated successfully', task });
  } catch (error) {
    errorHandler(error, res);
  }
};

const deleteTask = async (req, res) => {
  try {
    const { taskId } = req.params;
    const deletedTask = await Task.findByIdAndDelete(taskId);
    if (!deletedTask) {
      return res.status(404).json({ error: 'Task not found' });
    }
    return res.json({ message: 'Task deleted successfully' });
  } catch (error) {
    errorHandler(error, res);
  }
};

module.exports = {
  createTask,
  getAllTasks,
  getTaskById,
  updateTask,
  deleteTask,
};
