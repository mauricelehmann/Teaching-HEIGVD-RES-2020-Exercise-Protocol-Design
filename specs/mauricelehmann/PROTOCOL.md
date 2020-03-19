## Application serveur

### But
S'entrainer avec les fonctionnalités réseau de Java.

### Explication & Fonctionalités
L'application se lance et boucle dans l'attente qu'un client se connecte (avec netcat : localhost 2205)
Lorsque la connection est établi, l'application envoi un message de bienvenue au client puis attend de sa part une commande :
Trois commandes sont possible :
  - help : affiche l'aide
  - time : affiche la date & l'heure
  - joke : Affiche une blague aléatoire (qu'on a récupéré sur https://icanhazdadjoke.com/api)

