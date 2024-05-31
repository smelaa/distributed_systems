import multiprocessing
import os
import sys
import pika
import threading

from CONST import ADMIN_EXCHANGE_NAME, EXCHANGE_NAME, INJURIES


def get_results(ch, method, properties, body):
    print(f"\nReceived results: {body.decode()}")
    print("> ", end="", flush=True)


def get_admin_msg(ch, method, properties, body):
    print(f"\nAdmin msg: {body.decode()}")
    print("> ", end="", flush=True)


class InputReader(threading.Thread):
    def __init__(self, channel):
        super(InputReader, self).__init__()
        self.channel = channel
        self.reply_queue = None

    def run(self):
        print(
            f"Type the injury type {INJURIES} and patient's last name.\nFormat: INJURY LAST_NAME\nTo exit press CTRL+C"
        )
        while True:
            print("> ", end="")
            try:
                exam = input()
            except EOFError:
                break
            exam = exam.split(" ")
            injury = exam[0]
            if len(exam) != 2:
                print("Wrong input format")
                continue
            if injury not in INJURIES:
                print("Unknown injury")
                continue
            name = exam[1]
            self.channel.basic_publish(
                exchange=EXCHANGE_NAME,
                routing_key=injury,
                body=name,
                properties=pika.BasicProperties(
                    reply_to=self.reply_queue,
                ),
            )
            print(f"Sent {injury} exam request for {name}")


def main(
    connection: pika.BlockingConnection,
    channel,
    input_thread: threading.Thread,
):
    # deklaracja exchange
    channel.exchange_declare(exchange=EXCHANGE_NAME, exchange_type="topic")

    # deklaracja kolejki do nasłuchiwania wyników
    results_queue_name = f"results{multiprocessing.current_process().pid}"
    res = channel.queue_declare(queue=results_queue_name)
    input_thread.reply_queue = res.method.queue
    channel.queue_bind(
        exchange=EXCHANGE_NAME, queue=results_queue_name, routing_key=results_queue_name
    )

    # ustawienie nasłuchiwania wyników
    channel.basic_consume(
        queue=results_queue_name,
        auto_ack=True,
        on_message_callback=get_results,
    )

    # deklaracja kolejki do nasłuchiwania wiadomości od admina
    channel.exchange_declare(exchange=ADMIN_EXCHANGE_NAME, exchange_type="fanout")
    result = channel.queue_declare(queue="", exclusive=True)
    admin_queue_name = result.method.queue
    channel.queue_bind(exchange=ADMIN_EXCHANGE_NAME, queue=admin_queue_name)

    # ustawienie nasłuchiwania wiadomoci od admina
    channel.basic_consume(
        queue=admin_queue_name,
        auto_ack=True,
        on_message_callback=get_admin_msg,
    )

    # start wątku przetwarzającego input i nasłuchiwania wyników
    input_thread.start()
    channel.start_consuming()

    connection.close()
    input_thread.join()


if __name__ == "__main__":
    connection = pika.BlockingConnection(pika.ConnectionParameters("localhost"))
    channel = connection.channel()
    input_thread = InputReader(channel)
    try:
        main(connection, channel, input_thread)
    except KeyboardInterrupt:
        print("Interrupted")
        connection.close()
        input_thread.join()
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)
