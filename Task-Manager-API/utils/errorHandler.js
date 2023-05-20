const errorHandler = (err, res) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Internal server error' });
};

module.exports = errorHandler;
