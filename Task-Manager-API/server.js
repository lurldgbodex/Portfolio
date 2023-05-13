const express = require('express')
const config = require('./config/config');

const app = express();
const port = process.env.PORT || 3000

//start database
config.db_connect();

// start express Server
app.listen(port, () => {
    console.log(`Server started on port ${port}`)
})