# Zadanie I - aplikacyjne

## Subskrypcja na zdarzenia

Wynikiem prac ma być aplikacja klient-serwer w technologii gRPC. Klient powinien móc dokonywać subskrypcji na pewnego rodzaju zdarzenia. To, o czym mają one informować, jest w gestii Wykonawcy, np. o nadchodzącym wydarzeniu, którym jesteśmy zainteresowani ze względu na miejsce, czas, tematykę itp, o osiągnięciu określonych w żądaniu warunków pogodowych w danym miejscu, itp.

### Dodatkowe informacje i wymagania:

- Na pojedyncze zdarzenie może się zasubskrybować wielu odbiorców naraz.
- Może istnieć wiele niezależnych subskrypcji (tj. np. na wiele różnych instancji spotkań).
- Projektując protokół komunikacji pomiędzy stronami należy odpowiednio wykorzystać mechanizm strumieniowania (stream) - niedopuszczalny jest polling.
- Wiadomości mogą nadchodzić z różnymi odstępami czasowymi (w rzeczywistości nawet bardzo długimi), jednak na potrzeby demonstracji rozwiązania należy przyjąć interwał rzędu pojedynczych sekund.
- W definicji wiadomości przesyłanych do klienta należy wykorzystać pola liczbowe, enum, string, message - wraz z co najmniej jednym modyfikatorem repeated. Etap subskrypcji powinien w jakiś sposób precyzować, które powiadomienia danej usługi (spośród wszystkich) są dla odbiorcy interesujące (np. obejmować wskazanie miasta, którego warunki pogodowe nas interesują) i dany odbiorca powinien otrzymywać wyłącznie interesujące go powiadomienia.
- Dla uproszczenia realizacji zadania można (nie trzeba) pominąć funkcjonalność samego tworzenia instancji wydarzeń lub miejsc, których dotyczy subskrypcja i notyfikacja - może to być zawarte w pliku konfiguracyjnym, a nawet kodzie źródłowym strony serwerowej. Treść wysyłanych zdarzeń może być wynikiem działania bardzo prostego generatora.
- W realizacji należy zadbać o odporność komunikacji na błędy sieciowe (które można symulować czasowym gwałtownym wyłączeniem klienta lub serwera lub włączeniem zapory sieciowej). Ustanie przerwy w łączności sieciowej musi pozwolić na ponowne ustanowienie komunikacji bez konieczności restartu procesów. Wiadomości przeznaczone do dostarczenia powinny być buforowane przez serwer do czasu ponownego ustanowienia łączności. Rozwiązanie musi być także "NAT-friendly" (tj. uwzględniać rozważane na laboratorium sytuacje związane z translacją adresów, w tym podtrzymywaniem aktywności w kanale komunikacyjnym).

### Technologia middleware

gRPC

### Języki programowania

dwa różne (jeden dla klienta, drugi dla serwera)

# Zadanie II - infrastrukturalne

## Efektywne zarządzanie serwantami

Celem zadania jest demonstracja (na bardzo prostym przykładzie) mechanizmu zarządzania serwantami technologii Ice.

- Zadanie powinno mieć postać bardzo prostej aplikacji klient-serwer, w której strona serwerowa obsługuje wiele obiektów Ice.
- Obiekty middleware występujące w aplikacji są dwojakiego typu: część powinna być zrealizowana przy pomocy dedykowanego dla każdego z nich serwanta, druga część ma korzystać ze współdzielonego dla nich wszystkich serwanta.
- Zarządzanie serwantami ma być efektywne, np. dla dedykowanych serwantów, taki serwant jest instancjonowany dopiero w momencie pierwszego zapotrzebowania na niego.
- Interfejs IDL obiektu może być superprosty, choć ma implikować konkretny sposób realizacji serwanta (co należy umieć uzasadnić).
- Aplikacja kliencka powinna jedynie umożliwić zademonstrowanie funkcjonalności serwera. Logi na konsoli po stronie serwera powinny pozwolić się zorientować, na którym obiekcie i na którym serwancie zostało wywołane żądanie i kiedy nastąpiło instancjonowanie serwanta.
- W zadaniu trzeba korzystać bezpośrednio z mechanizmów zarządzania serwantami oferowanego przez technologię, a nie własnych, zbliżonych mechanizmów (w szczególności dotyczy to wykorzystania tablicy ASM).
- Każdy obiekt middleware musi być „osiągalny” przez klienta przez podanie jego identyfikatora (Identity).
- Należy zwrócić uwagę na działanie metod checkedCast oraz uncheckedCast – serwant musi być tworzony dopiero po wywołaniu operacji z biznesowego interfejsu obiektu (tj. IDL), na rzecz którego działa.
- Rozszerzeniem funkcjonalności systemu może być specjalizowany ewiktor usuwający z pamięci RAM najmniej potrzebne (np. najdawniej używane) serwanty (wraz z zachowaniem ich stanu) w razie przekroczenia zdefiniowanej, maksymalnej liczby serwantów.

### Technologia middleware

Ice

## Języki programowania

dwa różne (jeden dla klienta, drugi dla serwera)
