
# CovidOut - Attestation de déplacement

<a href='https://play.google.com/store/apps/details?id=com.nivelais.covidout&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' height='90' /></a>

Générateur d'attestations de déplacement dérogatoire.

Cette applications n'est pas celle du gouvernement.

Les attestations générer sont cependant identique a celle du gouvernement, vous pouvez le vérifier de vous même en scannant les codes barre générer et en analysant le PDF.

## Motivation

Pour passer le temps durant le confinement, autant produire une applications qui peut s’avérer utile et qui peut simplifier la vie des personnes qui ont la nécessite de sortir régulièrement.

## Partie techniques

Application développée en Kotlin, respectant la clean architecture de google et le pattern MVVM.

Les codes est donc séparer en 3 packages :

 - Presentation (Interface graphique, logique de la vue)
 - Common (Logique métier de l'application)
 - Data (Recuperation et génération de donner, utiliser par la partie common)

Librairie utilise dans l'application :
 - Koin (Injection de dépendance)
 - PDFBox Android (Génération de PDF)
 - ZXing (Génération de code barre)
 - ObjectBox (Base de donner, pour le stockage des attestations)
