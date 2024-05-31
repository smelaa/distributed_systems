const path = require("path");

const grpc = require("@grpc/grpc-js");
const protoLoader = require("@grpc/proto-loader");
const readline = require("readline");

const PROTO_PATH = path.join(__dirname, "../cafeteria.proto");
const packageDefinition = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true,
});
const cafeteria_proto = grpc.loadPackageDefinition(packageDefinition).cafeteria;

function listenSubscription(subscription) {
  subscription.on("data", (data) => {
    console.log("\n##Dish: " + data.dish_name);
    console.log("##Description: " + data.description);
    console.log("##Ingredients: ");
    data.ingredients.forEach((ingredient) => console.log(ingredient));
  });
  subscription.on("error", (e) => console.error("Error: " + e));
}

function main() {
  const cafeteriaService = new cafeteria_proto.CafeteriaService(
    "localhost:50051",
    grpc.credentials.createInsecure()
  );

  let rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
  });
  let subscriptions = [];

  console.log(
    "W [1-5] - subscribe to weekday\nC [1-4] - subscribe to cuisine\n"
  );

  rl.on("line", (line) => {
    line = line.trim().split(" ");

    let option = parseInt(line[1]);

    switch (line[0]) {
      case "W":
        if (option >= 1 || option <= 5) {
          listenSubscription(
            cafeteriaService.subscribeWeekday({
              weekday: option,
            })
          );
          //console.log(subscriptions.length);
        } else {
          console.log("Wrong weekday");
        }
        break;
      case "C":
        if (option >= 1 || option <= 5) {
          listenSubscription(
            cafeteriaService.subscribeCuisine({
              cuisine: option,
            })
          );
          //console.log(subscriptions.length);
        } else {
          console.log("Wrong cuisine");
        }
        break;
      default:
        console.log("???");
        break;
    }
  }).on("close", () => {
    console.log("Exiting...");
  });
}

if (require.main === module) {
  main();
}
