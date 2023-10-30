import redisClient from "../utils/redis";
import dbClient from "../utils/db";
import { ObjectID } from "mongodb";
import { v4 as uuidv4 } from "uuid";
import path from "path";
import fs, { promises as fspromise } from "fs";
import mime from "mime-types";
import { fileQueue } from "../worker";

const FilesController = {
  /**
   * postUpload - create a new file in db and disk
   *
   * @returns - newly created file
   */
  async postUpload(req, res) {
    try {
      const TYPE = { folder: "folder", file: "file", image: "image" };
      const { name, type, parentId = 0, isPublic = false, data } = req.body;
      const token = req.headers["x-token"];

      const key = `auth_${token}`;
      const userId = await redisClient.get(key);

      if (!userId) {
        return res.status(401).json({ error: "Unauthorized" });
      }

      if (!name) {
        return res.status(400).json({ error: "Missing name" });
      }
      if (!type || !Object.values(TYPE).includes(type)) {
        return res.status(400).json({ error: "Missing type" });
      }
      if (!data && type !== TYPE.folder) {
        return res.status(400).json({ error: "Missing data" });
      }
      const files = await dbClient.files();
      if (parentId !== 0) {
        const filesInParent = await files.findOne({ _id: ObjectID(parentId) });
        if (!filesInParent) {
          return res.status(400).json({ error: "Parent not found" });
        }
        if (filesInParent.type !== TYPE.folder) {
          return res.status(400).json({ error: "Parent is not a folder" });
        }
      }
      if (type === TYPE.folder) {
        const newFile = {
          userId: ObjectID(userId),
          name,
          type,
          parentId,
          isPublic,
        };
        const insertedFileRes = await files.insertOne(newFile);
        const fileId = insertedFileRes.insertedId;
        const createdfile = await files.findOne({ _id: fileId });
        return res.status(201).json(createdfile);
      } else {
        const folderPath = process.env.FOLDER_PATH || "/tmp/files_manager";
        const localPath = path.join(folderPath, uuidv4());

        await fspromise.mkdir(folderPath, { recursive: true });

        const decodedData = Buffer.from(data, "base64");
        await fspromise.writeFile(localPath, decodedData, "binary");

        const newFile = {
          userId: ObjectID(userId),
          name,
          type,
          isPublic,
          parentId,
          localPath,
        };
        const createdFile = await files.insertOne(newFile);
        const fileId = createdFile.insertedId;
        const insertedFile = await files.findOne({ _id: fileId });

        if (type === TYPE.image) {
          const job = await fileQueue.add({ userId, fileId });
          console.log(`Job ${job.id} added to fileQueue `);
        }

        return res.status(201).json(insertedFile);
      }
    } catch (err) {
      console.log(err);
      return res.status(500).json({ error: err.message });
    }
  },

  /**
   * getShow - get a file based on id
   *
   * @returns - the file
   */
  async getShow(req, res) {
    try {
      const { id } = req.params;
      const token = req.headers["x-token"];
      const key = `auth_${token}`;
      const userId = await redisClient.get(key);

      if (!userId) {
        return res.status(401).json({ error: "Unauthorized" });
      }
      const files = await dbClient.files();
      const file = await files.findOne({
        _id: ObjectID(id),
        userId: ObjectID(userId),
      });

      if (!file) {
        return res.status(404).json({ error: "Not found" });
      }
      return res.status(200).json(file);
    } catch (err) {
      return res.status(500).json({ error: err.message });
    }
  },

  /**
   * getIndex - get files based on parentid
   *
   * @returns - files gotten
   */
  async getIndex(req, res) {
    try {
      const token = req.headers["x-token"];
      const { parentId = 0, page } = req.query;
      const key = `auth_${token}`;
      const userId = await redisClient.get(key);
      if (!userId) {
        return res.status(401).json({ error: "Unauthorized" });
      }

      const pageNo = page ? parseInt(page) : 0;

      const pageSize = 20;
      const files = await dbClient.files();
      const filesOfParent = await files
        .aggregate([
          { $match: { parentId, userId: ObjectID(userId) } },
          { $sort: { _id: -1 } },
          { $skip: pageNo * pageSize },
          { $limit: pageSize },
        ])
        .toArray();

      return res.status(200).json(filesOfParent);
    } catch (err) {
      console.log(err);
      return res.status(500).json({ error: err.message });
    }
  },

  /**
   * putPublish - make a file public by updating the file's isPublic
   *
   * @returns - the file
   */
  async putPublish(req, res) {
    try {
      const { id } = req.params;
      const token = req.headers["x-token"];
      const key = `auth_${token}`;
      const userId = await redisClient.get(key);

      if (!userId) {
        return res.status(401).json({ error: "Unauthorized" });
      }

      const files = await dbClient.files();
      const file = await files.findOne({
        _id: ObjectID(id),
        userId: ObjectID(userId),
      });

      if (!file) {
        return res.status(404).json({ error: "Not found" });
      }
      await files.updateOne(
        { _id: ObjectID(id) },
        { $set: { isPublic: true } }
      );
      const updatedFile = await files.findOne({ _id: ObjectID(id) });

      return res.status(200).json(updatedFile);
    } catch (err) {
      return res.status(500).json({ error: err.message });
    }
  },

  /**
   * putUnpublish - Unpublish a file by updating the file's isPbulic
   *
   * @returns - updated file
   */
  async putUnpublish(req, res) {
    try {
      const { id } = req.params;
      const token = req.headers["x-token"];
      const key = `auth_${token}`;
      const userId = await redisClient.get(key);

      if (!userId) {
        return res.status(401).json({ error: "Unauthorized" });
      }

      const files = await dbClient.files();
      const file = await files.findOne({
        _id: ObjectID(id),
        userId: ObjectID(userId),
      });

      if (!file) {
        return res.status(404).json({ error: "Not found" });
      }

      await files.updateOne(
        { _id: ObjectID(id) },
        { $set: { isPublic: false } }
      );
      const updatedFile = await files.findOne({ _id: ObjectID(id) });

      return res.status(200).json(updatedFile);
    } catch (err) {
      return res.status(500).json({ error: err.message });
    }
  },

  /**
   * getFile - get the content of file
   *
   * @returns - files content
   */
  async getFile(req, res) {
    try {
      const TYPE = { folder: "folder", file: "file" };
      const { id } = req.params;
      const { size = "original" } = req.query;
      const validSizes = ["original", "500", "250", "100"];

      if (!validSizes.includes(size)) {
        return res.status(400).json({ error: "Invalid size parameter" });
      }

      const token = req.headers["x-token"];
      const key = `auth_${token}`;
      const userId = await redisClient.get(key);

      const files = await dbClient.files();
      const file = await files.findOne({ _id: ObjectID(id) });

      if (!file) {
        return res.status(404).json({ error: "Not found" });
      }

      const isOwner = await files.findOne({
        _id: ObjectID(id),
        userId: ObjectID(userId),
      });

      if (file.isPublic === false && (!userId || !isOwner)) {
        return res.status(404).json({ error: "Not found" });
      }

      if (file.type === TYPE.folder) {
        return res.status(400).json({ error: "A folder doesn't have content" });
      }

      if (!file.localPath) {
        return res.status(404).json({ error: "Not found" });
      }

      let filePath;
      if (size === "original") {
        filePath = file.localPath;
      } else {
        const dirName = path.dirname(file.localPath);
        const nameExtension = path.extname(file.localPath);
        const fileName = path.basename(file.localPath, nameExtension);
        const sizeSuffix = `_${size}`;
        filePath = path.join(
          dirName,
          `${fileName}${sizeSuffix}${nameExtension}`
        );
      }

      if (!fs.existsSync(filePath)) {
        return res.status(404).json({ error: "Not found" });
      }
      const mimeType = mime.lookup(file.name);
      res.setHeader("Content-Type", mimeType);

      fs.createReadStream(filePath).pipe(res);
    } catch (err) {
      return res.status(500).json({ error: err.message });
    }
  },
};

module.exports = FilesController;
