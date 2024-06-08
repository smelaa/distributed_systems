## Zadanie
1. Stworzyć aplikację w środowisku Zookeeper (Java, …), która wykorzystując mechanizm obserwatorów (watches) umożliwia następujące funkcjonalności:
    * Jeśli tworzony jest znode o nazwie „a” uruchamiana jest zewnętrzna aplikacja graficzna (dowolna, określona w linii poleceń),
    * Jeśli jest kasowany „a” aplikacja zewnętrzna jest zatrzymywana,
    * Każde dodanie potomka do „a” powoduje wyświetlenie graficznej informacji na ekranie o aktualnej ilości potomków.
2. Dodatkowo aplikacja powinna mieć możliwość wyświetlenia całej struktury drzewa „a”.
3. Stworzona aplikacja powinna działać w środowisku „Replicated ZooKeeper”.

### Wskazówki do uruchomienia ZooNavigator
* docker run -d -p 9000:9000 -e HTTP_PORT=9000 --name zoonavigator --restart unless-stopped elkozmon/zoonavigator:latest
* http://localhost:9000
* connection string: host.docker.internal:[PORT], np. host.docker.internal:2181