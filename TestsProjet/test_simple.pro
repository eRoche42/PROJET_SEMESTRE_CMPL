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