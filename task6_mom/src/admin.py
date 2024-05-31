import os
import sys
import threading
import pika

from CONST import ADMIN_EXCHANGE_NAME, EXCHANGE_NAME


def log_callback(ch, method, properties, body):
    print(f"\nLog: {body.decode()}")
    print("> ", end="", flush=True)


class InputReader(threading.Thread):
    def __init__(self, channel):
        super(InputReader, self).__init__()
        self.channel = channel

    def run(self):
        print(f"Type a message to be sent to broadcast\nTo exit press CTRL+C")
        channel.exchange_declare(exchange=ADMIN_EXCHANGE_NAME, exchange_type="fanout")
        while True:
            print("> ", end="")
            try:
                msg = input()
            except EOFError:
                break
            channel.basic_publish(
                exchange=ADMIN_EXCHANGE_NAME, routing_key="", body=msg
            )
            print(f"Sent message")


def main(connection: pika.BlockingConnection, channel, input_thread):

    # deklaracja exchange
    channel.exchange_declare(exchange=EXCHANGE_NAME, exchange_type="topic")

    # ustawienie nasłuchiwania
    channel.queue_declare(queue="log")
    channel.queue_bind(exchange=EXCHANGE_NAME, queue="log", routing_key="*")
    channel.basic_consume(
        queue="log",
        auto_ack=True,
        on_message_callback=log_callback,
    )

    print("Started...")

    # start wątku wysyłającego wiadokości
    input_thread.start()

    # start nasłuchiwania
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
