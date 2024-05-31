import os
import sys
import pika
import time

from CONST import ADMIN_EXCHANGE_NAME, EXCHANGE_NAME, INJURIES


def perform_exam(ch, method, properties, body):
    patient = body.decode()
    injury = method.routing_key
    print(f"Received request for: \t {patient} {injury}")
    time.sleep(3)
    response = f"{patient} {injury} done"

    # Wysyłanie wyników
    ch.basic_publish(
        exchange=EXCHANGE_NAME, routing_key=properties.reply_to, body=response
    )

    print(f"Sent results for: \t {patient} {injury}")


def get_admin_msg(ch, method, properties, body):
    print(f"Admin msg: {body.decode()}")


def main(connection: pika.BlockingConnection, injury_type1, injury_type2):
    channel = connection.channel()

    # deklaracja exchange
    channel.exchange_declare(exchange=EXCHANGE_NAME, exchange_type="topic")

    # ustawienie nasłuchiwania requestów
    for exam_type in (injury_type1, injury_type2):
        channel.queue_declare(queue=exam_type)
        channel.queue_bind(
            exchange=EXCHANGE_NAME, queue=exam_type, routing_key=exam_type
        )
        channel.basic_consume(
            queue=exam_type,
            auto_ack=True,
            on_message_callback=perform_exam,
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

    print("Started...\nTo exit press CTRL+C")

    # start nasłuchiwania requestów
    channel.start_consuming()

    connection.close()


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print(
            "Wrong number of arguments - to run call 'python technican.py injury_type1 injury_type2', where injury_typen is a value from (knee, elbow, hip)"
        )
        exit()
    injury_type1 = sys.argv[1]
    injury_type2 = sys.argv[2]

    if injury_type1 not in INJURIES or injury_type2 not in INJURIES:
        print(
            "Wrong arguments - to run call 'python technican.py injury_type1 injury_type2', where injury_typen is a value from (knee, elbow, hip)"
        )
        exit()

    connection = pika.BlockingConnection(pika.ConnectionParameters("localhost"))
    try:
        main(connection, injury_type1, injury_type2)
    except KeyboardInterrupt:
        print("Interrupted")
        connection.close()
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)
