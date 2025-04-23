import java.io.*;
import java.util.ArrayList;
 /**
 * 
 * @author Roche Elie, Rio Evan
 * @version 2025
 *
 */


public class Edl {



	private static class ElemDef {
		// nomProc = nom de la procedure definie en DEF
		public String nomProc;
		// adPo = adresse de debut de code de cette procedure
		// nbParam =  nombre de parametres de cette procedure
		public int adPo, nbParam;

		public ElemDef(String nomProc, int adPo, int nbParam) {
			this.nomProc = nomProc;
			this.adPo = adPo;
			this.nbParam = nbParam;
		}
	}
	
	






	// nombre max de modules, taille max d'un code objet d'une unite
	static final int MAXMOD = 5, MAXOBJ = 1000;
	// nombres max de references externes (REF) et de points d'entree (DEF)
	// pour une unite
	private static final int MAXREF = 10, MAXDEF = 10;

	// typologie des erreurs
	private static final int FATALE = 0, NONFATALE = 1;

	// valeurs possibles du vecteur de translation
	private static final int TRANSDON=1,TRANSCODE=2,REFEXT=3;

	// table de tous les descripteurs concernes par l'edl
	static Descripteur[] tabDesc = new Descripteur[MAXMOD + 1];

	//TODO : declarations de variables A COMPLETER SI BESOIN
	static int ipo, nMod, nbErr;
	static String nomProg;

	
	static int[] transDon = new int[MAXMOD+1];
	static int[] transCode = new int[MAXMOD+1];
	static ArrayList<ElemDef> dicoDef = new ArrayList<>();
	static int [][] adFinale = new int[MAXMOD+1][];


	// utilitaire de traitement des erreurs
	// ------------------------------------
	static void erreur(int te, String m) {
		System.out.println(m);
		if (te == FATALE) {
			System.out.println("ABANDON DE L'EDITION DE LIENS");
			System.exit(1);
		}
		nbErr = nbErr + 1;
	}

	// utilitaire de remplissage de la table des descripteurs tabDesc
	// --------------------------------------------------------------
	static void lireDescripteurs() {
		String s;
		System.out.println("les noms doivent etre fournis sans suffixe");
		System.out.print("nom du programme : ");
		s = Lecture.lireString();
		tabDesc[0] = new Descripteur();
		tabDesc[0].lireDesc(s);
		if (!tabDesc[0].getUnite().equals("programme"))
			erreur(FATALE, "programme attendu");
		nomProg = s;

		nMod = 0;
		while (!s.equals("") && nMod < MAXMOD) {
			System.out.print("nom de module " + (nMod + 1)
					+ " (RC si termine) ");
			s = Lecture.lireString();
			if (!s.equals("")) {
				nMod = nMod + 1;
				tabDesc[nMod] = new Descripteur();
				tabDesc[nMod].lireDesc(s);

				if (!tabDesc[nMod].getUnite().equals("module"))
					erreur(FATALE, "module attendu");
			}
		}
	}


	static void constMap() {
		// f2 = fichier executable .map construit
		OutputStream f2 = Ecriture.ouvrir(nomProg + ".map");
		if (f2 == null)
			erreur(FATALE, "creation du fichier " + nomProg
					+ ".map impossible");
		// pour construire le code concatene de toutes les unités
		int[] po = new int[(nMod + 1) * MAXOBJ + 1];
		
		transDon[0] = 0;
		transCode[0] = 0;
		//Décallage des variables global.
		int index = 0;
		for (Descripteur i : tabDesc) {
			index++;
			if ( i != null){
				transDon[index] = i.getTailleGlobaux() + transDon[index-1] ;
				transCode[index] = i.getTailleCode() + transCode[index-1];
				int nbProc = i.getNbDef();
				for (int u = 0 ; u <nbProc;u++){
					String nomDef = i.getDefNomProc(u)+index;
					int nbParam = i.getDefNbParam(u);
					int adPo = i.getDefAdPo(u);
					ElemDef k = new ElemDef(nomDef, adPo, nbParam);
					dicoDef.addLast(k);
				}
			}
		}
		index = 0;
		
		for (Descripteur i :  tabDesc){
			int nbRef = i.getNbRef();
			if (nbRef != 0){
				adFinale[index] = new int[nbRef];
				for (int u = 0 ; u <nbRef;u++){
					String nomDef = i.getRefNomProc(u);
					int nbParam = i.getRefNbParam(u);
					//adFinal
				}


			}
			index++;


		}

		//Construction du dictionnaire.


		//TODO : ... A COMPLETER ...
		// 
		//

		Ecriture.fermer(f2);

		// creation du fichier en mnemonique correspondant
		Mnemo.creerFichier(ipo, po, nomProg + ".ima");
	}

	public static void main(String argv[]) {
		System.out.println("EDITEUR DE LIENS / PROJET LICENCE");
		System.out.println("---------------------------------");
		System.out.println("");
		nbErr = 0;

		// Phase 1 de l'edition de liens
		// -----------------------------
		lireDescripteurs();		//TODO : lecture des descripteurs a completer si besoin

		//TODO : ... A COMPLETER ...
		// 
		// 
		//

		if (nbErr > 0) {
			System.out.println("programme executable non produit");
			System.exit(1);
		}

		// Phase 2 de l'edition de liens
		// -----------------------------
		constMap();				//TODO : ... A COMPLETER ...
		System.out.println("Edition de liens terminee");
	}
}
