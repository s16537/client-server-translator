Programowanie klient-serwer z użyciem gniazd i protokołów sieciowycych (bez kanałów i  selektorów)

Zaprojektuj aplikację umożliwiającą korzystanie z usługi słownikowej w różnych językach. Struktura aplikacji powinna składać się, min. z

* serwera głównego,

* klienta z prostym GUI,

* serwerów odpowiadających się za słowniki w poszczególnych językach.

Każdy serwer słownikowy przechowuje dane do słownika w jednym języku, dla uproszczenia można przyjąć, że są to pary {polskie hasło, tłumaczenie}. Języki są identyfikowane za pomocą krótkich kodów (np. "PL", "EN", "FR", ...). Aplikacja powinna umożliwić proste dodawanie obsługi/serwerów nowych języków.

Klient przekazuje do serwera głównego zapytanie w postaci {"polskie słowo do przetłumaczenia", "kod języka docelowego", port}. Port, na którym klient oczekuje na tłumaczenie zostanie zamknięty po nadejściu informacji od serwera słownikowego.

Serwer główny wysyła do konkretnego serwera słownikowego komunikat w postaci {"polskie słowo do przetłumaczenia", adres klienta, port na którym klient czeka na wynik}. Następuje połączenie od serwera słownikowego do klienta oraz przekazywanie wyniku tłumaczenia, po czym to połączenie zostanie zamknięte.

Można założyć, że maszyny serwera głównego, klientów, serwerów językowych są wzajemnie dostępne.

Pożądana jest możliwość równoległej obsługi wielu klientów.

Proste GUI klienta powinno być odseparowane od logiki działania.

Należy zadbać o obsługę sytuacji wyjątkowych.
