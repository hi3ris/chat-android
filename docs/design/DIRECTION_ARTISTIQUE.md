# Direction Artistique — Kdodo
## Application de messagerie sécurisée by CYBER DEFENSE AFRICA

> Ce document décrit les choix visuels de l'application Kdodo (Android).
> Il sert de référence pour l'implémentation iOS et toute évolution future de l'interface.

---

## 1. Identité de marque

### Nom
**Kdodo** — messagerie instantanée sécurisée, développée par CYBER DEFENSE AFRICA (CDA).

### Valeurs visuelles
- **Sobriété** — pas de couleurs criardes, l'interface s'efface derrière le contenu
- **Confiance** — codes couleurs issus de la charte officielle CDA
- **Modernité** — inspiré du langage visuel Apple (iOS/macOS)
- **Cohérence** — trois couleurs de marque, rôles stricts, aucune improvisation

---

## 2. Couleurs de marque CDA

### Palette officielle

| Nom | HEX | RGB | Usage principal |
|---|---|---|---|
| **Vert CDA** | `#006A4E` | 0, 106, 78 | Accent, boutons, actions |
| **Rouge CDA** | `#D21034` | 210, 16, 52 | Erreurs, alertes critiques |
| **Jaune CDA** | `#FFCE00` | 255, 206, 0 | Information, highlights |

### Références physiques
| Couleur | Pantone | CMJN | RAL |
|---|---|---|---|
| Vert CDA | 3298 C | 100, 0, 27, 58 | 6016 (Vert émeraude) |
| Rouge CDA | ~Rouge Britannique | 0, 92, 75, 18 | — |
| Jaune CDA | 049 C | 0, 19, 100, 0 | — |

---

## 3. Principes de design

### Inspiration : Apple Design Language
L'interface Kdodo adopte les codes visuels d'Apple :
- **Fonds neutres et chauds** — jamais de noir pur (`#000000`) ni de blanc cassé grisâtre
- **Hiérarchie typographique forte** — tailles et graisses clairement différenciées
- **Couleurs en touches** — les couleurs de marque sont utilisées avec parcimonie, jamais en aplat généralisé
- **Séparateurs ultra-fins** — les éléments sont délimités subtilement, pas par des bordures épaisses
- **Coins arrondis généreux** — cartes, bulles, boutons

### Règle des 3 couleurs
```
VERT  → Je peux agir    (boutons, icônes actives, bulles envoyées)
JAUNE → Je suis informé (bannières info, notifications secondaires)
ROUGE → Attention       (erreurs, actions destructives, alertes)
```

Le reste de l'interface est en **gris neutres** (Apple Gray System).

---

## 4. Système de couleurs

### Mode clair (Light)

| Rôle | HEX | Usage |
|---|---|---|
| Fond principal | `#FFFFFF` | Fond de l'app, liste des rooms |
| Fond secondaire | `#F2F2F7` | Fond des sections groupées, cartes |
| Fond tertiaire | `#E5E5EA` | Bulles reçues, éléments en retrait |
| Séparateur | `#E5E5EA` | Lignes de séparation entre items |
| Texte primaire | `#000000` | Titres, noms de contacts |
| Texte secondaire | `#3C3C43` | Aperçu du dernier message, sous-titres |
| Texte tertiaire | `#8E8E93` | Horodatage, labels désactivés |
| **Accent (vert)** | `#006A4E` | Boutons CTA, bulles envoyées, icônes actives |
| Accent hover | `#005C43` | État survolé / pressé |
| Info (jaune) | `#FFCE00` | Icônes info, bannières, indicateurs |
| Erreur (rouge) | `#D21034` | Messages d'erreur, boutons destructifs |

### Mode sombre (Dark) — Apple Dark System

| Rôle | HEX | Équivalent Apple | Usage |
|---|---|---|---|
| Fond principal | `#1C1C1E` | systemBackground | Fond de l'app |
| Fond secondaire | `#2C2C2E` | secondarySystemBackground | Cartes, items de liste |
| Fond tertiaire | `#3A3A3C` | tertiarySystemBackground | Bulles reçues |
| Fond quaternaire | `#48484A` | systemFill | Éléments en relief |
| Séparateur | `#38383A` | separator | Lignes entre items |
| Texte primaire | `#FFFFFF` | label | Noms, titres |
| Texte secondaire | `#AEAEB2` | secondaryLabel | Aperçus, sous-titres |
| Texte tertiaire | `#8E8E93` | tertiaryLabel | Horodatage |
| **Accent (vert)** | `#0A9A72` | — | Version lumineuse du vert CDA sur fond sombre |
| Info (jaune) | `#FFCE00` | — | Identique light/dark |
| Erreur (rouge) | `#E83055` | — | Version lumineuse du rouge CDA |

---

## 5. Composants UI

### Barre de navigation du bas (Tab Bar)

| État | Icône | Texte |
|---|---|---|
| Sélectionné | Vert CDA `#006A4E` plein | Texte primaire |
| Non sélectionné | Vert CDA `#006A4E` à 50% opacité | Texte désactivé |
| Indicateur de sélection | Aucun (transparent) | — |

**Onglets présents :**
- 💬 Chats (Discussions)
- 🏠 Espaces (Rooms)

### Boutons

| Type | Fond | Texte | Usage |
|---|---|---|---|
| **Primaire** | Vert `#006A4E` | Blanc `#FFFFFF` | Action principale (connexion, envoi) |
| **Accent** | Vert `#006A4E` | Blanc `#FFFFFF` | CTA secondaire (rejoindre, créer) |
| **Secondaire** | Transparent | Vert `#006A4E` | Action alternative |
| **Destructif** | Rouge `#D21034` | Blanc `#FFFFFF` | Supprimer, déconnecter |
| **Désactivé** | Gris `#8E8E93` | Blanc `#FFFFFF` | Action non disponible |

### Bulles de message

| Type | Mode clair | Mode sombre |
|---|---|---|
| **Envoyé** (utilisateur) | Fond `#006A4E`, texte blanc | Fond `#0A9A72`, texte blanc |
| **Reçu** (contact) | Fond `#E5E5EA`, texte noir | Fond `#3A3A3C`, texte blanc |
| **Système / info** | Fond `#F2F2F7`, texte `#3C3C43` | Fond `#2C2C2E`, texte `#AEAEB2` |

### Badges / Compteurs non-lus

- Fond : Vert CDA `#006A4E`
- Texte : Blanc `#FFFFFF`
- Forme : Cercle (1 chiffre) ou pill arrondie (2+ chiffres)

### Écran de démarrage (Splash Screen)

| Mode | Couleur |
|---|---|
| Clair | `#FFFFFF` |
| Sombre | `#1C1C1E` (seamless avec le fond de l'app) |

---

## 6. Typographie

> Note : Android utilise **Roboto** par défaut. iOS utilisera **SF Pro** (système).
> Les principes hiérarchiques restent identiques entre les deux plateformes.

| Niveau | Taille | Graisse | Usage |
|---|---|---|---|
| Large Title | 34pt | Bold | Titres d'écran principaux |
| Title 1 | 28pt | Bold | Titres de section |
| Title 2 | 22pt | Bold | Sous-titres importants |
| Headline | 17pt | Semibold | Nom du contact dans la liste |
| Body | 17pt | Regular | Corps des messages |
| Callout | 16pt | Regular | Aperçu du dernier message |
| Subhead | 15pt | Regular | Informations secondaires |
| Footnote | 13pt | Regular | Horodatage, statuts |
| Caption | 12pt | Regular | Labels très secondaires |

---

## 7. Iconographie

- **Style** : Outline (inactif) → Solid/Filled (actif) — même principe qu'iOS SF Symbols
- **Couleur inactive** : Vert CDA 50% opacité
- **Couleur active** : Vert CDA 100%
- **Icônes d'action** (appel, vidéo, info dans top bar) : Vert CDA ou Jaune CDA selon le contexte
- **Icônes d'alerte / erreur** : Rouge CDA

---

## 8. Ce qu'on NE fait PAS

| Interdit | Raison |
|---|---|
| Fond noir pur `#000000` en dark mode | Trop agressif, non Apple |
| Couleurs saturées en aplat généralisé | Casse la sobriété |
| Mélanger rouge + jaune + vert sans raison | Confusion sémantique |
| Texte rouge pour tout ce qui est "important" | Rouge = erreur critique uniquement |
| Bordures épaisses sur les cartes | Style non Apple |

---

## 9. Application iOS — Points d'attention

### Correspondances de tokens

| Rôle Android (Compound) | Équivalent iOS (SwiftUI/UIKit) |
|---|---|
| `colorThemeBg` dark = `#1C1C1E` | `UIColor.systemBackground` dark |
| `colorGray200` dark = `#2C2C2E` | `UIColor.secondarySystemBackground` dark |
| `colorGray300` dark = `#3A3A3C` | `UIColor.tertiarySystemBackground` dark |
| `colorGray200` light = `#F2F2F7` | `UIColor.secondarySystemBackground` light |
| `colorGray300` light = `#E5E5EA` | `UIColor.tertiarySystemBackground` light |
| `bgAccentRest` = `#006A4E` | `UIColor(hex: "006A4E")` — tint color global |
| `textPrimary` | `UIColor.label` |
| `textSecondary` | `UIColor.secondaryLabel` |

### Recommandations SwiftUI

```swift
// Couleurs de marque CDA
extension Color {
    static let cdaGreen  = Color(hex: "006A4E")
    static let cdaRed    = Color(hex: "D21034")
    static let cdaYellow = Color(hex: "FFCE00")

    // Variantes dark mode
    static let cdaGreenDark  = Color(hex: "0A9A72")
    static let cdaRedDark    = Color(hex: "E83055")
}

// Tint global de l'app
.accentColor(.cdaGreen)

// Tab bar icons
.foregroundStyle(isSelected ? Color.cdaGreen : Color.cdaGreen.opacity(0.5))

// Bouton primaire
.background(Color.cdaGreen)
.foregroundStyle(.white)
```

---

## 10. Checklist de conformité

Avant toute livraison, vérifier :

- [ ] Boutons primaires en vert CDA `#006A4E`
- [ ] Fond dark mode en `#1C1C1E` (pas noir pur)
- [ ] Icônes de navigation en vert CDA
- [ ] Badges en vert CDA
- [ ] Erreurs en rouge CDA `#D21034` uniquement
- [ ] Bannières info en jaune CDA `#FFCE00`
- [ ] Bulles envoyées en vert CDA
- [ ] Splash screen sans logo Element, fond blanc/`#1C1C1E`
- [ ] Aucune mention "Element" visible dans l'UI
- [ ] Nom de l'app : **Kdodo** (pas Element X, pas Kdodo X)

---

*Document maintenu par l'équipe CYBER DEFENSE AFRICA — https://www.cda.tg*
*Dernière mise à jour : mars 2026*
