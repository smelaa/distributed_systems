# Temat zadania
Celem zadania jest napisanie prostego serwisu webowego realizującego pewną złożoną funkcjonalność w oparciu o otwarte serwisy udostępniające REST API. Stworzyć serwis, który:

* udostępni klientowi statyczną stronę HTML z formularzem do zebrania parametrów żądania,
* odbierze zapytanie od klienta,
* odpyta serwis publiczny (różne endpointy), a lepiej kilka serwisów o dane potrzebne do skonstruowania odpowiedzi,
* dokona odróbki otrzymanych odpowiedzi (np.: wyciągnięcie średniej, znalezienie ekstremów, porównanie wartości z różnych serwisów itp.),
* wygeneruje i wyśle odpowiedź do klienta (statyczna strona HTML z wynikami).

Realizowanej funkcjonalność i używane serwisy do wyboru. 
Przykładowo:
Klient podaje miasto i okres czasu ('daty od/do' lub 'ostatnie n dni'), serwer odpytuje ogólnodostępny serwis pogodowy o temperatury w poszczególne dni, oblicza średnią i znajduje ekstrema i wysyła do klienta wszystkie wartości (tzn. prostą stronę z tymi danymi). Ewentualnie serwer odpytuje kilka serwisów pogodowych i podaje różnice w podawanych prognozach.
Listę różnych publicznych API można znaleźć np.: na https://publicapis.dev/

### Wymagania (czyli jeszcze raz i bardziej szczegółowo)
* Klient (przeglądarka) ma wysyłać żądanie w oparciu o dane z formularza (statyczny HTML) i otrzymać odpowiedź w formie prostej strony www, wygenerowanej przez tworzony serwis. Wystarczy użyć czystego HTML, bez stylizacji, bez dodatkowych bibliotek frontendowych (nie jest to elementem oceny). Nie musi być piękne, ma działać.
* Tworzony serwis powinien wykonać kilka zapytań (np.: o różne dane, do kilku serwisów itp.). Niech Państwa rozwiązanie nie będzie tylko takim proxy do jednego istniejącego serwisu i niech zapewnia dodatkową logikę (to będzie elementem oceny, najlepiej 2 lub więcej).
* Odpowiedź dla klienta musi być generowana przez serwer na podstawie: 
1) żądań REST do publicznych serwisów i 
2) lokalnej obróbki uzyskanych odpowiedzi.
* Serwer ma być uruchomiony na własnym serwerze aplikacyjnym działającym poza IDE (lub analogicznej technologii).
* Dodatkowym (ale nieobowiązkowym) atutem jest wystawienie serwisu w chmurze (np.: Heroku). To jest część dla zainteresowanych i nie podlega podstawowej ocenie.
* Dopuszczalna jest realizacja zadania w dowolnym wybranym języku/technologii (oczywiście sugerowany jest Python i FastAPI). Proszę jednak o zachowanie analogicznego poziomu abstrakcji (operowanie bezpośrednio na żądaniach/odpowiedziach HTTP, kontrola generowania/odbierania danych).
* Serwer który udostępnia API musi być zgodny z REST.
* Implementacja elementów bezpieczeństwa API jest częścią oceny.
* Wybieramy serwisy otwarte lub dające dostęp ograniczony, lecz darmowy, np.: używające kodów deweloperskich.
* Dodatkowo (jest to elementem oceny): Przygotowujemy test zapytań HTTP z wykorzystaniem POSTMANa/SwaggerUI (klient-serwer i serwer-serwis_publiczny). Do oddania proszę mieć je już zapisane.
### Na co warto zwrócić uwagę?
* (!!) obsługę (a)synchroniczności zapytań serwera do serwisów zewnętrznych (np.: promises),
* (!) obsługę błędów i odpowiedzi z serwisów (np.: jeśli pojawi się błąd komunikacji z serwisem zewnętrznym, to generujemy odpowiedni komunikat do klienta, a nie 501 Internal server error),
* walidację danych wprowadzanych przez klienta/przyjmowanych przez serwer.
### Punktacja (0-10 pkt.)
* Implementacja serwera - obsługa zapytań do zewnętrznego serwisu: [0-2] pkt.
* Implementacja serwera - odbiór żądań klienta zgodna z REST, generowanie i wysłanie odpowiedzi: [0-2] pkt.
* Implementacja serwera - obsługa asynchroniczności zapytań i błędów [0-3]
* Implementacja klienta - statyczny formularz zapytań / strona odpowiedzi: [0-2] pkt.
* Testowanie żądań REST z pomocą Postman-a/Swager UI (do serwera i do serwisu zewnętrznego): [0-1] pkt