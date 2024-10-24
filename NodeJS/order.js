const mongoose = require("mongoose");
// mongoose.connect("mongodb://localhost:27017/dummy");

const OrderSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    unique: true,
  },
  order: String,
  quantity: Number,
});


module.exports = mongoose.model("sales", OrderSchema);  