package service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDate;
import models.*;

/**
 * Service gérant la persistance des données dans des fichiers texte.
 * Gère le chargement et la sauvegarde de toutes les entités du système.
 */
public class FileManager {
    // Chemins des fichiers de données
    private static final String DATA_DIR = "data/";
    private static final String ENTERPRISES_FILE = DATA_DIR + "entreprises.txt";
    private static final String CANDIDATS_FILE = DATA_DIR + "candidats.txt";
    private static final String OFFRES_FILE = DATA_DIR + "offres.txt";
    private static final String FORUM_FILE = DATA_DIR + "commentaires.txt";

    public FileManager() {
        createDataDirectory();
    }

    /**
     * Crée le dossier data s'il n'existe pas.
     */
    private void createDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.out.println("Erreur création dossier data: " + e.getMessage());
        }
    }

    // ========== CHARGEMENT DES DONNÉES ==========
    
    /**
     * Charge les entreprises depuis le fichier.
     * Format : nom|secteur|adresse|email|telephone|mdp
     */
    public List<Entreprise> chargerEntreprises() {
        List<Entreprise> entreprises = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(ENTERPRISES_FILE))) {
            String line;
            
            // Lire ligne par ligne
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                
                // Format avec mot de passe (6 champs)
                if (parts.length >= 6) {
                    Entreprise e = new Entreprise(
                        parts[0], // nom
                        parts[1], // secteur
                        parts[2], // adresse
                        parts[3], // email
                        parts[4], // telephone
                        parts[5]  // mdp
                    );
                    entreprises.add(e);
                } 
                // Ancien format sans mot de passe (5 champs) - Compatibilité
                else if (parts.length >= 5) {
                    Entreprise e = new Entreprise(
                        parts[0], parts[1], parts[2], parts[3], parts[4],
                        "default123" // Mot de passe par défaut
                    );
                    entreprises.add(e);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fichier entreprises non trouvé, création...");
        } catch (IOException e) {
            System.out.println("Erreur lecture entreprises: " + e.getMessage());
        }
        
        return entreprises;
    }

    /**
     * Charge les candidats depuis le fichier.
     * Format nouveau : id|type|nom|prenom|email|telephone|mdp|...infos spécifiques
     * Supporte aussi l'ancien format pour compatibilité.
     */
    public List<Candidat> chargerCandidats() {
        List<Candidat> candidats = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(CANDIDATS_FILE))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                
                // Nouveau format avec ID et mot de passe
                if (parts.length >= 9) {
                    int id = Integer.parseInt(parts[0]);
                    String type = parts[1];
                    String nom = parts[2];
                    String prenom = parts[3];
                    String email = parts[4];
                    String telephone = parts[5];
                    String mdp = parts[6];
                    
                    // Créer selon le type
                    if (type.equals("etudiant") && parts.length >= 10) {
                        Etudiant etud = new Etudiant(
                            id, nom, prenom, email, telephone, mdp,
                            parts[7], // niveau
                            parts[8], // filiere
                            parts[9], // etablissement
                            candidats
                        );
                        candidats.add(etud);
                    } else if (type.equals("alumni") && parts.length >= 10) {
                        Alumni alumni = new Alumni(
                            id, nom, prenom, email, telephone, mdp,
                            Integer.parseInt(parts[7]), // anneeDiplome
                            parts[8], // posteActuel
                            parts[9], // entrepriseActuelle
                            candidats
                        );
                        candidats.add(alumni);
                    } else if (parts.length >= 7) {
                        Candidat c = new Candidat(
                            id, nom, prenom, email, telephone, mdp, candidats
                        );
                        candidats.add(c);
                    }
                }
                // Ancien format (sans ID et mdp) - Compatibilité
                else if (parts.length >= 4) {
                    // Générer un ID temporaire
                    int id = 10000000 + candidats.size();
                    String mdp = "default123";
                    
                    if (parts.length == 7) { // Étudiant ancien format
                        Etudiant etud = new Etudiant(
                            id, parts[0], parts[1], parts[2], parts[3], mdp,
                            parts[4], parts[5], parts[6], candidats
                        );
                        candidats.add(etud);
                    } else if (parts.length == 6) { // Alumni ancien format
                        Alumni alumni = new Alumni(
                            id, parts[0], parts[1], parts[2], parts[3], mdp,
                            Integer.parseInt(parts[4]), parts[5], "", candidats
                        );
                        candidats.add(alumni);
                    } else { // Candidat simple ancien format
                        Candidat c = new Candidat(
                            id, parts[0], parts[1], parts[2], parts[3], mdp, candidats
                        );
                        candidats.add(c);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fichier candidats non trouvé, création...");
        } catch (IOException e) {
            System.out.println("Erreur lecture candidats: " + e.getMessage());
        }
        
        return candidats;
    }

    /**
     * Charge les offres depuis le fichier.
     * Format : type|titre|description|emailEntreprise|datePublication|dateExpiration|...infos spécifiques
     */
    public List<Offre> chargerOffres(List<Entreprise> entreprises) {
        List<Offre> offres = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(OFFRES_FILE))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                
                if (parts.length >= 6) {
                    String typeOffre = parts[0];
                    String titre = parts[1];
                    String description = parts[2];
                    String emailEntreprise = parts[3];
                    String datePublication = parts[4];
                    String dateExpiration = parts[5];

                    // Trouver l'entreprise correspondante
                    Entreprise entreprise = null;
                    for (Entreprise e : entreprises) {
                        if (e.getEmail().equals(emailEntreprise)) {
                            entreprise = e;
                            break;
                        }
                    }

                    // Créer l'offre si entreprise trouvée
                    if (entreprise != null) {
                        Offre offre = null;

                        switch (typeOffre.toLowerCase()) {
                            case "stage":
                                if (parts.length >= 8) {
                                    offre = new Stage(
                                        titre, description, entreprise,
                                        Integer.parseInt(parts[6]), // durée
                                        parts[7] // domaine
                                    );
                                }
                                break;

                            case "alternance":
                                if (parts.length >= 8) {
                                    offre = new Alternance(
                                        titre, description, entreprise,
                                        parts[6], // rythme
                                        Integer.parseInt(parts[7]) // durée
                                    );
                                }
                                break;

                            case "projet fin d'etudes":
                                if (parts.length >= 8) {
                                    offre = new ProjetFinEtudes(
                                        titre, description, entreprise,
                                        parts[6], // sujet
                                        parts[7]  // technologies
                                    );
                                }
                                break;

                            default:
                                offre = new Offre(titre, description, typeOffre, entreprise);
                                break;
                        }

                        if (offre != null) {
                            // Définir la date d'expiration si présente
                            if (!dateExpiration.equals("null")) {
                                offre.setDateExpiration(LocalDate.parse(dateExpiration));
                            }
                            offres.add(offre);

                            // Rattacher l'offre à l'entreprise
                            entreprise.getOffresPubliees().add(offre);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fichier offres non trouvé, création...");
        } catch (IOException e) {
            System.out.println("Erreur lecture offres: " + e.getMessage());
        }
        
        return offres;
    }
    
    /**
     * Charge les commentaires du forum depuis le fichier.
     * Format : auteur|email|message|estEtudiant|datePublication
     */
    public List<Forum> chargerCommentaires() {
        List<Forum> commentaires = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(FORUM_FILE))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                
                if (parts.length >= 5) {
                    Forum forum = new Forum(
                        parts[0], // auteur
                        parts[1], // email
                        parts[2], // message
                        Boolean.parseBoolean(parts[3]) // estEtudiant
                    );
                    commentaires.add(forum);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fichier commentaires non trouvé, création...");
        } catch (IOException e) {
            System.out.println("Erreur lecture commentaires: " + e.getMessage());
        }
        
        return commentaires;
    }

    // ========== SAUVEGARDE DES DONNÉES ==========
    
    /**
     * Sauvegarde les entreprises dans le fichier.
     */
    public void sauvegarderEntreprises(List<Entreprise> entreprises) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ENTERPRISES_FILE))) {
            // Écrire chaque entreprise
            for (Entreprise e : entreprises) {
                pw.println(String.join("|",
                    e.getNom(),
                    e.getSecteur(),
                    e.getAdresse(),
                    e.getEmail(),
                    e.getTelephone(),
                    e.getMdp()
                ));
            }
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde entreprises: " + e.getMessage());
        }
    }

    /**
     * Sauvegarde les candidats dans le fichier.
     */
    public void sauvegarderCandidats(List<Candidat> candidats) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CANDIDATS_FILE))) {
            // Écrire chaque candidat selon son type
            for (Candidat c : candidats) {
                if (c instanceof Etudiant) {
                    Etudiant etud = (Etudiant) c;
                    pw.println(String.join("|",
                        String.valueOf(etud.getId()),
                        "etudiant",
                        etud.getNom(),
                        etud.getPrenom(),
                        etud.getEmail(),
                        etud.getTelephone(),
                        etud.getMdp(),
                        etud.getNiveau(),
                        etud.getFiliere(),
                        etud.getEtablissement()
                    ));
                } else if (c instanceof Alumni) {
                    Alumni alumni = (Alumni) c;
                    pw.println(String.join("|",
                        String.valueOf(alumni.getId()),
                        "alumni",
                        alumni.getNom(),
                        alumni.getPrenom(),
                        alumni.getEmail(),
                        alumni.getTelephone(),
                        alumni.getMdp(),
                        String.valueOf(alumni.getAnneeDiplome()),
                        alumni.getPosteActuel(),
                        alumni.getEntrepriseActuelle()
                    ));
                } else {
                    pw.println(String.join("|",
                        String.valueOf(c.getId()),
                        "simple",
                        c.getNom(),
                        c.getPrenom(),
                        c.getEmail(),
                        c.getTelephone(),
                        c.getMdp()
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde candidats: " + e.getMessage());
        }
    }

    /**
     * Sauvegarde les offres dans le fichier.
     */
    public void sauvegarderOffres(List<Offre> offres) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(OFFRES_FILE))) {
            // Écrire chaque offre
            for (Offre o : offres) {
                String type = o.getTypeOffre();
                
                // Ligne de base commune
                String ligne = type + "|" +
                    o.getTitre() + "|" +
                    o.getDescription() + "|" +
                    o.getEntreprise().getEmail() + "|" +
                    o.getDatePublication().toString() + "|" +
                    (o.getDateExpiration() != null ? o.getDateExpiration().toString() : "null");

                // Ajouter attributs spécifiques selon le type
                if (o instanceof Stage) {
                    Stage s = (Stage) o;
                    ligne += "|" + s.getDureeEnMois() + "|" + s.getDomaine();
                } else if (o instanceof Alternance) {
                    Alternance a = (Alternance) o;
                    ligne += "|" + a.getRythme() + "|" + a.getDureeEnMois();
                } else if (o instanceof ProjetFinEtudes) {
                    ProjetFinEtudes p = (ProjetFinEtudes) o;
                    ligne += "|" + p.getSujet() + "|" + p.getTechnologies();
                }

                pw.println(ligne);
            }
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde offres: " + e.getMessage());
        }
    }

    /**
     * Sauvegarde les commentaires du forum dans le fichier.
     */
    public void sauvegarderCommentaires(List<Forum> commentaires) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FORUM_FILE))) {
            // Écrire chaque commentaire
            for (Forum f : commentaires) {
                pw.println(String.join("|",
                    f.getAuteur(),
                    f.getEmailAuteur(),
                    f.getMessage(),
                    String.valueOf(f.isEstEtudiant()),
                    f.getDatePublication().toString()
                ));
            }
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde commentaires: " + e.getMessage());
        }
    }
}