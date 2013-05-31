lanciare il registrar con

    python registrar_server.py


* poi lanciare due peer, in questo modo:

    ./roundword.sh <nickname> <porta> <url registrar>

ad esempio

    ./roundword.sh Ford86 4821 http://localhost:8080

* per lanciare un peer che gioca in automatico

    ./roundword.sh <nickname> <porta> <url registrar> ai

    ad esempio

    ./roundword.sh Grassone99 4821 http://localhost:8080 ai

* per lanciare un peer e far scegliere con una GUI i parametri

    ./roundword.sh