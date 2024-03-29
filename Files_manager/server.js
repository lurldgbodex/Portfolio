import express from "express";

const app = express();
const port = process.env.PORT || 5000;

app.use(express.json({ limit: "50mb" }));

app.use("/", require("./routes/index"));

app.listen(port, () => {
  console.log("app listening on port", port);
});

module.exports = app;
