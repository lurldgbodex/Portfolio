import Queue from "bull";
import imageThumbnail from "image-thumbnail";
import { ObjectId } from "mongodb";
import fs from "fs";

import dbClient from "./utils/db";
import redisClient from "./utils/redis";
import path from "path";

const fileQueue = new Queue("fileQueue", redisClient);
const userQueue = new Queue("userQueue", redisClient);

fileQueue.process(async (job) => {
  const { fileId, userId } = job.data;

  if (!fileId) {
    throw new Error("Missing fileId");
  }
  if (!userId) {
    throw new Error("Mising userId");
  }

  const files = await dbClient.files();
  const file = await files.findOne({
    _id: ObjectId(fileId),
    userId: ObjectId(userId),
  });

  if (!file) {
    throw new Error("File not found");
  }

  const sizes = [500, 250, 100];

  try {
    for (const size of sizes) {
      const thumbnail = await imageThumbnail(file.localPath, { width: size });
      const fileExtension = path.extname(file.localPath);
      const thumbnailFilenName =
        path.basename(file.localPath, fileExtension) +
        `_${size}${fileExtension}`;
      const thumbnailPath = path.join(
        path.dirname(file.localPath),
        thumbnailFilenName
      );
      fs.writeFileSync(thumbnailPath, thumbnail);
      console.log(`Generated thumbnails for file ${fileId}`);
    }
  } catch (err) {
    console.log(err.message);
  }
});

userQueue.process(async (job) => {
  const { userId } = job.data;
  if (!userId) {
    throw new Error("Missing userId");
  }

  const users = await dbClient.users();
  const user = await users.findOne({ _id: ObjectId(userId) });

  if (!user) {
    throw new Error("User not found");
  }
  console.log(`Welcome ${user.email}`);
});

module.exports = { fileQueue, userQueue };
