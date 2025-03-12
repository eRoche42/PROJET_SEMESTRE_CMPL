=============================
|     L3   Groupe   4.1     |
=============================
|  Elie ROCHE  |  Evan RIO  |
=============================
|      Compilateur  V1      |
=============================

Notre V1 couvre la compilation des :

- déclarations de variables et constantes globales
  { entières et booléenes }

- expressions
  { =   <>   >   >=   <   <=   +   -   *   div }

- instructions suivantes 
  { if   cond   ttq   lire   ecrire   := }



Question: 

Nous avons une question sur l'utilisation d'écriture,
durant les tests les valeurs retournées par tcour venant de variable booléenes, 
sont matché comme des entiers et non comme des booléens , l'écriture ce passe bien car 
V correspond a 1 et F correspond a 0. 
le type affiché suite à l'éxécution correspond bien a un bool pourtant
Est-ce normal ?

Preuve d'éxecution:
 (programme test_simple)
    code           categorie      type    info
      |--------------|--------------|-------|----
bc=  1 @b1            VARGLOBALE     BOOL    0
     2 @b2            VARGLOBALE     BOOL    1
     3 @b3            VARGLOBALE     BOOL    2
     4 @b4            VARGLOBALE     BOOL    3
     5 @b5            VARGLOBALE     BOOL    4
it=  6 @b6            VARGLOBALE     BOOL    5


exec Mapile:

Donner un booléen (V ou F) : F
Donner un booléen (V ou F) : V
Donner un booléen (V ou F) : V
Donner un booléen (V ou F) : V
Donner un booléen (V ou F) : V
Donner un booléen (V ou F) : V
Expression entière de valeur : 0  // ici le 0 correspond a un true de la variable bool b1


source:

programme test: {expression simple}

var bool b1, b2, b3, b4, b5, b6;	{adresses variables dans la pile d'exécution: 0, 1, 2, 3, 4, 5}
debut
	lire(b1,b2,b3,b4,b5,b6);
	cond
		b1: ecrire(1),
		b2: ecrire(3),
		b3: ecrire(b1)
		aut ecrire(5)
	fcond;
fin
