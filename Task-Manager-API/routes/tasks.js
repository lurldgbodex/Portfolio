const express = require('express')
const TaskController = require('../controllers/taskController')
const authenticateToken = require('../middleware/authMiddleware')


const router = express.Router();

router.post('/tasks', authenticateToken, TaskController.createTask);
router.get('/tasks', authenticateToken, TaskController.getAllTasks);
router.get('/tasks/:taskId', authenticateToken, TaskController.getTaskById)
router.patch('/tasks/:taskId', authenticateToken, TaskController.updateTask)
router.delete('/tasks/:taskId', authenticateToken, TaskController.deleteTask)

module.exports = router;