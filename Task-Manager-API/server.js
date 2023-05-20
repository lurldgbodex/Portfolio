const express = require('express');
const config = require('./config/config');

const app = express();
const port = process.env.PORT || 3000;

// start database
config.dbConnect();

// set up middlewares
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// set up routes
app.use('/api/users', require('./routes/auth'));
app.use('/api', require('./routes/tasks'));

// start express Server
app.listen(port, () => {
  console.log(`Server started on port ${port}`);
});

module.exports = app;
