package com.IHEC.Recruit.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Advice transversal appliqué automatiquement à tous les controllers Spring MVC.
 *
 * <p>Expose les attributs de session courants dans le modèle Thymeleaf,
 * évitant ainsi la duplication des méthodes {@code getXxxFromSession()} dans
 * chaque controller individuel.</p>
 *
 * <p>Attributs injectés dans chaque vue :
 * <ul>
 *   <li>{@code currentUserType} — "candidat" | "entreprise" | null</li>
 *   <li>{@code currentUserNom}  — prénom + nom du candidat, ou nom de l'entreprise</li>
 *   <li>{@code currentUserId}   — Integer (CIN) pour un candidat,
 *                                  Long pour une entreprise</li>
 * </ul>
 * </p>
 */
@ControllerAdvice
public class SessionControllerAdvice {

    /**
     * Expose le type de l'utilisateur connecté ("candidat" ou "entreprise")
     * dans le modèle de chaque vue.
     *
     * @param session la session HTTP courante
     * @return la valeur de l'attribut de session "userType", ou {@code null}
     */
    @ModelAttribute("currentUserType")
    public String currentUserType(HttpSession session) {
        return (String) session.getAttribute("userType");
    }

    /**
     * Expose le nom affiché de l'utilisateur connecté dans le modèle de chaque vue.
     *
     * <p>Pour un candidat : "Prénom Nom".
     * Pour une entreprise : nom de l'entreprise.</p>
     *
     * @param session la session HTTP courante
     * @return la valeur de l'attribut de session "userNom", ou {@code null}
     */
    @ModelAttribute("currentUserNom")
    public String currentUserNom(HttpSession session) {
        return (String) session.getAttribute("userNom");
    }

    /**
     * Expose l'identifiant de l'utilisateur connecté dans le modèle de chaque vue.
     *
     * <ul>
     *   <li>Candidat  → {@code Integer} stocké sous la clé "candidatId"</li>
     *   <li>Entreprise → {@code Long}    stockée sous la clé "entrepriseId"</li>
     * </ul>
     *
     * <p>La méthode retourne le premier identifiant non-null trouvé,
     * en donnant la priorité au candidat.</p>
     *
     * @param session la session HTTP courante
     * @return l'identifiant (Integer ou Long) de l'utilisateur connecté,
     *         ou {@code null} si personne n'est connecté
     */
    @ModelAttribute("currentUserId")
    public Object currentUserId(HttpSession session) {
        Object candidatId = session.getAttribute("candidatId");
        if (candidatId != null) return candidatId;
        return session.getAttribute("entrepriseId");
    }
}