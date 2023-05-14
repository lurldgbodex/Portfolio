const Task = require('../models/Task');

const createTask = async(req, res) => {
    try {
        const { title, description } = req.body;
        const newTask = new Task({
            title,
            description
        });
        await newTask.save();
        return res.json({ message: 'Task created successfully', task: newTask });

    } catch (error) {
        console.log(error.message)
        res.status(500).json({ error: 'Internal server error' });
    }   
};

const getAllTasks = async(req, res) => {
    try {
        const tasks = await Task.find();
        return res.json(tasks);
    } catch (error) {
        return res.status(500).json({ error: 'Internal server error' });
    }
};

const getTaskById = async(req, res) => {
    try {
        const { id } = req.params
        const task = await Task.findById(id);
        if (!task) {
            return res.status(404).json({ error: 'Task not found' });
        }
        return res.json(task);

    } catch (error) {
        return res.status(500).json({ error: 'Internal server error'})
    }
};

const updateTask = async(req, res) => {
    try {
        const { id } = req.params;
        const { title, description, status } = req.body;

        const task = await Task.findById(id);
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
        return res.json({ message: 'Task updated successfully', task })

    } catch (error) {
        console.log(error.message)
        return res.status(500).json({ error: 'Internal server error' });
    }
};

const deleteTask = async(req, res) => {
    try {
        const { id } = req.params
        const deletedTask = await Task.findByIdAndDelete(id);
        if (!deletedTask) {
            return res.status(404).json({ error: 'Task not found' });
        };

        return res.json({ message: 'Task deleted successfully' })

    } catch (error) {
        return res.status(500).json({ error: 'Internal server error' });
    }
}

module.exports = { 
    createTask, 
    getAllTasks, 
    getTaskById, 
    updateTask, 
    deleteTask
};