#Roche elie.
#Rio Evan
#Grp 4.1

#Mot-clef:
JAVAC = javac
JAVA = java
SRC_DIR = src
BIN_DIR = bin
LIB_DIR = lib
MAIN_CLASS = Mapile
ARCHIVE_NAME = projet_compile_ROCHE_RIO_$(shell date +%Y%m%d).zip

#Chemin
CP = $(LIB_DIR)/*:$(BIN_DIR)
SOURCES = $(shell find $(SRC_DIR) -name "*.java")
CLASSES = $(SOURCES:$(SRC_DIR)/%.java=$(BIN_DIR)/%.class)






all: clean compile run 

# Compilation 
# Output dans bin en .class

compile:
	@echo "🔨 Compilation en cours..."
	@mkdir -p $(BIN_DIR)  # Check bin
	@find $(SRC_DIR) -name "*.java" > sources.tmp  # Liste tous les fichiers Java
	@$(JAVAC) -cp "$(CP)" -d $(BIN_DIR) @sources.tmp
	@rm sources.tmp
	@echo "✅ Compilation terminée."

# Execution
run:
	@echo "🔍 Recherche des classes contenant un point d'entrée..."
	@find $(BIN_DIR) -name "*.class" | sed 's|$(BIN_DIR)/||;s|.class||' > class_list.tmp
	@grep -l "public static void main" $(SRC_DIR)/*.java | sed 's|$(SRC_DIR)/||;s|.java||' > main_classes.tmp

	@if [ -s main_classes.tmp ]; then \
		echo "🚀 Classes exécutables détectées :"; \
		cat main_classes.tmp | nl; \
		echo "🔢 Choisissez un numéro pour exécuter une classe :"; \
		read choice; \
		main_class=$$(sed "$$choice!d" main_classes.tmp); \
		rm main_classes.tmp;\
		rm class_list.tmp;\
		echo "▶ Exécution de $$main_class..."; \
		$(JAVA) -cp "$(CP)" $$main_class; \
	else \
		echo "❌ Aucune classe avec main détectée."; \
	fi

# Clean
clean:
	@echo "🗑 Nettoyage des fichiers compilés..."
	@rm -rf $(BIN_DIR)/*
	@echo "✅ Dossier bin vidé."

# L'aide
help:
	@echo "📜 Commandes disponibles :"
	@echo "  make          - Compile et exécute le programme"
	@echo "  make compile  - Compile le projet"
	@echo "  make run      - Exécute le projet"
	@echo "  make clean    - Supprime les fichiers compilés"
	@echo "	 make zip 	   - Archive le projet"
	@echo "  make help     - Affiche cette aide"

#Crée archive sans la liste d'exclusion ci dessous dans la commande zip
zip:
	@echo "📦 Création de l'archive $(ARCHIVE_NAME)..."
	@zip -r $(ARCHIVE_NAME) . -x ".git/*" ".gitignore" "*.zip" "class_list.tmp" "main_classes.tmp"
	@echo "✅ Archive créée : $(ARCHIVE_NAME)"
