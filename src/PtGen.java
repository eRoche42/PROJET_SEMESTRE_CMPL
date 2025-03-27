/*********************************************************************************
 * VARIABLES ET METHODES FOURNIES PAR LA CLASSE UtilLex (cf libClass_Projet)     *
 *       complement à l'ANALYSEUR LEXICAL produit par ANTLR                      *
 *                                                                               *
 *                                                                               *
 *   nom du programme compile, sans suffixe : String UtilLex.nomSource           *
 *   ------------------------                                                    *
 *                                                                               *
 *   attributs lexicaux (selon items figurant dans la grammaire):                *
 *   ------------------                                                          *
 *     int UtilLex.valEnt = valeur du dernier nombre entier lu (item nbentier)   *
 *     int UtilLex.numIdCourant = code du dernier identificateur lu (item ident) *
 *                                                                               *
 *                                                                               *
 *   methodes utiles :                                                           *
 *   ---------------                                                             *
 *     void UtilLex.messErr(String m)  affichage de m et arret compilation       *
 *     String UtilLex.chaineIdent(int numId) delivre l'ident de codage numId     *
 *     void afftabSymb()  affiche la table des symboles                          *
 *********************************************************************************/


import java.io.*;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * classe de mise en oeuvre du compilateur
 * =======================================
 * (verifications semantiques + production du code objet)
 * 
 * @author Girard, Masson, Perraudeau
 *
 */

public class PtGen {
    

    // constantes manipulees par le compilateur
    // ----------------------------------------

	private static final int 
	
	// taille max de la table des symboles
	MAXSYMB=300,

	// codes MAPILE :
	RESERVER=1,EMPILER=2,CONTENUG=3,AFFECTERG=4,OU=5,ET=6,NON=7,INF=8,
	INFEG=9,SUP=10,SUPEG=11,EG=12,DIFF=13,ADD=14,SOUS=15,MUL=16,DIV=17,
	BSIFAUX=18,BINCOND=19,LIRENT=20,LIREBOOL=21,ECRENT=22,ECRBOOL=23,
	ARRET=24,EMPILERADG=25,EMPILERADL=26,CONTENUL=27,AFFECTERL=28,
	APPEL=29,RETOUR=30,

	// codes des valeurs vrai/faux
	VRAI=1, FAUX=0,

    // types permis :
	ENT=1,BOOL=2,NEUTRE=3,

	// categories possibles des identificateurs :
	CONSTANTE=1,VARGLOBALE=2,VARLOCALE=3,PARAMFIXE=4,PARAMMOD=5,PROC=6,
	DEF=7,REF=8,PRIVEE=9,

    //valeurs possible du vecteur de translation 
    TRANSDON=1,TRANSCODE=2,REFEXT=3;


    // utilitaires de controle de type
    // -------------------------------
    /**
     * verification du type entier de l'expression en cours de compilation 
     * (arret de la compilation sinon)
     */
	private static void verifEnt() {
		if (tCour != ENT)
			UtilLex.messErr("expression entiere attendue");
	}
	/**
	 * verification du type booleen de l'expression en cours de compilation 
	 * (arret de la compilation sinon)
	 */
	private static void verifBool() {
		if (tCour != BOOL)
			UtilLex.messErr("expression booleenne attendue");
	}

    // pile pour gerer les chaines de reprise et les branchements en avant
    // -------------------------------------------------------------------

    private static TPileRep pileRep;  


    // production du code objet en memoire
    // -----------------------------------

    private static ProgObjet po;
    
    
    // COMPILATION SEPAREE 
    // -------------------
    //
    /** 
     * modification du vecteur de translation associe au code produit 
     * + incrementation attribut nbTransExt du descripteur
     *  NB: effectue uniquement si c'est une reference externe ou si on compile un module
     * @param valeur : TRANSDON, TRANSCODE ou REFEXT
     */
    private static void modifVecteurTrans(int valeur) {
		if (valeur == REFEXT || desc.getUnite().equals("module")) {
			po.vecteurTrans(valeur);
			desc.incrNbTansExt();
		}
	}    
    // descripteur associe a un programme objet (compilation separee)
    private static Descripteur desc;

     
    // autres variables fournies
    // -------------------------
    
 // MERCI de renseigner ici un nom pour le trinome, constitue EXCLUSIVEMENT DE LETTRES
    public static String trinome="ROCHE_RIO"; 	//TODO 
    
    private static int tCour; // type de l'expression compilee
    private static int vCour; // sert uniquement lors de la compilation d'une valeur (entiere ou boolenne)
	
    private static int reservNumber;
    private static int nbArgz;
	
   
    // TABLE DES SYMBOLES
    // ------------------
    //
    private static EltTabSymb[] tabSymb = new EltTabSymb[MAXSYMB + 1];
    
    // it = indice de remplissage de tabSymb
    // bc = bloc courant (=1 si le bloc courant est le programme principal)
	private static int it, bc;

	private static int iAddrExec ;
	
	/** 
	 * utilitaire de recherche de l'ident courant (ayant pour code UtilLex.numIdCourant) dans tabSymb
	 * 
	 * @param borneInf : recherche de l'indice it vers borneInf (=1 si recherche dans tout tabSymb)
	 * @return : indice de l'ident courant (de code UtilLex.numIdCourant) dans tabSymb (O si absence)
	 */
	private static int presentIdent(int borneInf) {
		int i = it;
		while (i >= borneInf && tabSymb[i].code != UtilLex.numIdCourant)
			i--;
		if (i >= borneInf)
			return i;
		else
			return 0;
	}

	/**
	 * utilitaire de placement des caracteristiques d'un nouvel ident dans tabSymb
	 * 
	 * @param code : UtilLex.numIdCourant de l'ident
	 * @param cat : categorie de l'ident parmi CONSTANTE, VARGLOBALE, PROC, etc.
	 * @param type : ENT, BOOL ou NEUTRE
	 * @param info : valeur pour une constante, ad d'exécution pour une variable, etc.
	 */
	private static void placeIdent(int code, int cat, int type, int info) {
		if (it == MAXSYMB)
			UtilLex.messErr("debordement de la table des symboles");
		it = it + 1;
		tabSymb[it] = new EltTabSymb(code, cat, type, info);
	}

	/**
	 *  utilitaire d'affichage de la table des symboles
	 */
	private static void afftabSymb() { 
		System.out.println("       code           categorie      type    info");
		System.out.println("      |--------------|--------------|-------|----");
		for (int i = 1; i <= it; i++) {
			if (i == bc) {
				System.out.print("bc=");
				Ecriture.ecrireInt(i, 3);
			} else if (i == it) {
				System.out.print("it=");
				Ecriture.ecrireInt(i, 3);
			} else
				Ecriture.ecrireInt(i, 6);
			if (tabSymb[i] == null)
				System.out.println(" reference NULL");
			else
				System.out.println(" " + tabSymb[i]);
		}
		System.out.println();
	}
    

	private static TPileRep procPile;

	/**
	 *  initialisations A COMPLETER SI BESOIN
	 *  -------------------------------------
	 */
	public static void initialisations() {
	
		// indices de gestion de la table des symboles
		it = 0;
		bc = 1;
		
		// pile des reprises pour compilation des branchements en avant
		pileRep = new TPileRep(); 
		// programme objet = code Mapile de l'unite en cours de compilation
		po = new ProgObjet();
		// COMPILATION SEPAREE: desripteur de l'unite en cours de compilation
		desc = new Descripteur();
		
		// initialisation necessaire aux attributs lexicaux
		UtilLex.initialisation();
	
		// initialisation du type de l'expression courante
		tCour = NEUTRE;

		iAddrExec = 0;

		//Réservation 
		reservNumber = 0;
		nbArgz = 0;
		procPile = new TPileRep();

	} // initialisations

	/**
	 *  code des points de generation A COMPLETER
	 *  -----------------------------------------
	 * @param numGen : numero du point de generation a executer
	 */
	public static void pt(int numGen) {
	
		switch (numGen) {
		case 0:
			initialisations();
			break;

		// Déclarations (penser à afficher tabSymb pour vérifier)

		// CONSTANTE
		case 1 :
			if( presentIdent(bc)>0 ) {
				UtilLex.messErr("Réaffectation de constantes interdite"); // Envoi Erreur
				break;
			}
			if(bc !=1){
				placeIdent(UtilLex.numIdCourant, VARLOCALE, tCour, iAddrExec++);
			}else{
				placeIdent(UtilLex.numIdCourant, CONSTANTE, tCour, vCour);

			}
			break;

		// VARGLOBALE
		case 2 :
		if( presentIdent(bc)>0 ) {
			UtilLex.messErr("Ident déjà déclaré"); // Envoi Erreur
			break;
		}
			if(bc != 1){
				placeIdent(UtilLex.numIdCourant, VARLOCALE, tCour, iAddrExec++);
				reservNumber += 1;
			}else{
			placeIdent(UtilLex.numIdCourant, VARGLOBALE, tCour, iAddrExec++);
			reservNumber += 1;}
			break;
		// LIRE Const ENT Positif
		case 3 :
			vCour = UtilLex.valEnt;
			break;

		// LIRE Const ENT Négatif
		case 4:
			vCour = -UtilLex.valEnt;
			break;
		// LIRE BOOL TRUE
		case 5:
			vCour = VRAI;
			break;
		// LIRE BOOL FALSE
		case 6:
			vCour = FAUX;
			break;
		// TYPE
		//ENTIER
		case 7:
			tCour = ENT;
			break;
		//BOOLEAN
		case 8:
			tCour = BOOL;
			break;
		// expression empilage Valeur
		case 9:
		    po.produire(EMPILER);
			po.produire(vCour);
		break;
		// expression avec un IDENT
		case 10:
            int index = presentIdent(1);

            if(index == 0) UtilLex.messErr("Non présent dans tab symb");

            if(tabSymb[index].categorie == CONSTANTE){
                po.produire(EMPILER);
                po.produire(tabSymb[index].info);
            }else{
                if (bc == 1) 
				{
					po.produire(CONTENUG);
					po.produire(tabSymb[index].info);

				}
                else {
					po.produire(CONTENUL);
					po.produire(tabSymb[index].info);

                if (tabSymb[index].categorie == PARAMMOD) po.produire(1);
                else po.produire(0);
				}
            }
            break;
		//OU
		case 11 :
			po.produire(OU);
		break;
		//ET
		case 12 :
			po.produire(ET);
		break;
		//NON
		case 13 :
			po.produire(NON);
		break;
		// ==
		case 14 :
			po.produire(EG);
		break;
		// <>
		case 15 :
			po.produire(DIFF);
		break;
		// >
		case 16 :
			po.produire(SUP);
		break;
		// >=
		case 17 :
			po.produire(SUPEG);
		break;
		// <
		case 18 :
			po.produire(INF);
		break;
		// <=
		case 19 :
			po.produire(INFEG);
		break;
		// +
		case 20 :
			po.produire(ADD);
		break;
		// -
		case 21 :
			po.produire(SOUS);
		break;
		// *
		case 22 :
			po.produire(MUL);
		break;
		// div
		case 23 :
			po.produire(DIV);
		break;

		//affecter
		case 24 :
            int index2 = presentIdent(1);
            if(index2 == 0) {UtilLex.messErr("Ident non présent dans tabSymb");}
            else{
				System.out.println(bc);
                if (bc == 1) {
                    pileRep.empiler(tabSymb[index2].info);
                    pileRep.empiler(AFFECTERG);
                } else {
					System.out.println("Dans La fonction " + po.getIpo());

                    if (tabSymb[index2].categorie == PARAMFIXE) UtilLex.messErr("L'affectation d'un paramètre fixe est interdite");
                    else if (tabSymb[index2].categorie == VARLOCALE) pileRep.empiler(0);
                    else 
					{pileRep.empiler(1);
                   
					}
					pileRep.empiler(tabSymb[index2].info);
                    pileRep.empiler(AFFECTERL);
                }
            }
            break;
        //fin affecter
        case 25:
            po.produire(pileRep.depiler());
            po.produire(pileRep.depiler());
            if (bc != 1) po.produire(pileRep.depiler());
        break;

		// BOUCLE
		//-------------------------------------------------
		case 26:
			pileRep.empiler(po.getIpo());
		break;
		
		case 27:
			po.produire(BSIFAUX);
			po.produire(1); //Dummy

			pileRep.empiler(po.getIpo());
		break;

		case 28:
			po.produire(BINCOND);
			// + 2 padding pour atteindre l'instruction suivante
			po.modifier(pileRep.depiler(), po.getIpo() + 2);
			// +1 padding.
			po.produire(pileRep.depiler() + 1);
		break;
		//--------------------------------------------------
		//isscond
		case 29:
		po.produire(BSIFAUX);
		po.produire(1);//Dummy
		pileRep.empiler(po.getIpo());
		break;
		case 30:
		po.produire(BINCOND);
		po.produire(1); //Dummy
		po.modifier(pileRep.depiler(), po.getIpo()+1);	
		pileRep.empiler(po.getIpo());
		break;
		case 31:
		po.modifier(pileRep.depiler(), po.getIpo()+1);
		break;
		//-------------------------------------------------
		// 'cond' {PtGen.pt(32);}
        case 32:
            pileRep.empiler(0);
            break;

        //    'cond' expression {PtGen.pt(33);} : instructions (, expression {PtGen.pt(33);} : instructions)*
        case 33:
            po.produire(BSIFAUX);
            po.produire(0);
            pileRep.empiler(po.getIpo());
            break;

        case 34:
            po.produire(BINCOND);
            // modif bsifaux precedent avec l'adresse des prochaines instructions
            po.modifier(pileRep.depiler(), po.getIpo()+2);
            // dépilement pour produire l'adresse du bincond courant
            po.produire(pileRep.depiler());
            // empilement de l'adresse du bincond courant
            pileRep.empiler(po.getIpo());
            break;

        case 35:
            // attraper et sauver l'ipo de fin de case
            // sauver l'argument du bincond précédent
            //
            int ipoAmodifier = pileRep.depiler();
            int  mem = po.getElt(ipoAmodifier);
			
            while (mem != 0) {
                mem = po.getElt(ipoAmodifier);
                po.modifier(ipoAmodifier, po.getIpo()-2);
                ipoAmodifier = mem;
            }
				mem = po.getElt(ipoAmodifier);
                po.modifier(ipoAmodifier, po.getIpo()-2);
            break;
		// LIRE :
        case 40:
            int indexLire = presentIdent(1);
            if(indexLire == 0) UtilLex.messErr("lire(): La variable "+ UtilLex.numIdCourant +" n'existe pas");
            if(tabSymb[indexLire].categorie == CONSTANTE) UtilLex.messErr("lire(): "+UtilLex.numIdCourant+" est une constante !");;
            switch (tabSymb[indexLire].type) {
                // lirent
                case ENT :
                    po.produire(LIRENT);
                    break;
                // lirebool
                case BOOL :
                    po.produire(LIREBOOL);
                    break;
            }
            // affecterg 'addrExec'
            po.produire(AFFECTERG);
            po.produire(tabSymb[indexLire].info);
            break;

        // ECRIRE :
        case 41:
			int indexLireE = presentIdent(1);
            switch (tabSymb[indexLireE].type) {
                case ENT :
                    po.produire(ECRENT);
                    break;
                case BOOL :
                    po.produire(ECRBOOL);
                    break;
            }

            break;
			// var extend a changer de place


		//Debut PROC
		case 50 :
			po.produire(BINCOND);
			po.produire(0);
			pileRep.empiler(po.getIpo());
			pileRep.empiler(iAddrExec);
			iAddrExec = 2;


		break;
			//Ajout de l'ident PARAMFIX
		case 51:
		placeIdent(UtilLex.numIdCourant, PARAMFIXE, tCour, nbArgz++);
		iAddrExec++;
		break;
			//Ajout de l'ident PARAMMOD
		case 52:
		placeIdent(UtilLex.numIdCourant, PARAMMOD, tCour, nbArgz++);
		iAddrExec++;

		break;

		case 53:	
			placeIdent(UtilLex.numIdCourant, PROC, NEUTRE, po.getIpo()+1);
			placeIdent(-1, PRIVEE, NEUTRE, 0);
			procPile.empiler(it);

			
		break;

		case 54:
				bc = it +1;
			break;
		//Fin proc
		case 55:
		iAddrExec = pileRep.depiler();
		tabSymb[procPile.depiler()].info = nbArgz;
		afftabSymb();
		bc = 1;
		
			while (tabSymb[it].categorie == VARLOCALE) {
				tabSymb[it].code = -1;
				it-- ;
				
			}
			
		po.produire(RETOUR);
		po.produire(nbArgz);
		nbArgz = 0;
		break;
		case 56:
		break;
		
		case 57:
		int index4 = presentIdent(1);
		if(index4 == 0) UtilLex.messErr("lire(): La variable "+ UtilLex.numIdCourant +" n'existe pas");
			nbArgz = 0;
			procPile.empiler(index4);
		break;
		case 58:
			nbArgz +=1;
		break;
		//APPEL
		case 59:
			po.produire(APPEL);
			po.produire(nbArgz);
			po.produire(tabSymb[procPile.depiler()].info);
			break;
		
		case 60:
			po.modifier(pileRep.depiler(), po.getIpo()+1);
		break;
		case 61:
		int index3 = presentIdent(1);
            if(index3 == 0) {UtilLex.messErr("Ident non présent dans tabSymb");}
		po.produire(EMPILERADG);
		po.produire(tabSymb[index3].info);
		break;
		case 254 :
			po.produire(RESERVER);
			po.produire(reservNumber);
			reservNumber = 0 ;
			break;
		case 255 : 
			po.produire(ARRET);
			po.constGen();
			po.constObj();
			
			afftabSymb(); // affichage de la table des symboles en fin de compilation
			break;

		
		default:
			System.out.println("Point de generation non prevu dans votre liste");
			break;

		}
	}
}
    
    
    
    
    
    
    
    
    
    
    
    
    
 
