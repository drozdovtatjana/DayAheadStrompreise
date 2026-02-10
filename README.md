EN

To Run the Programm: 

Programming flow: Console app first ...
Programming Language:

Architectural Style:

Design Patterns: 

OOP usage:

Robust Data: try-catch

Data Normalization:

Libraries:

Why not Docker:

Winter/Summer Time explanation:

Testing:

Missing data on some hours:

Code Structure Diagramm:

Bugs to Fix:

Possible Improments:



DE


Die Normalisierung erfolgt anhand eines expliziten Stundenrasters
auf Basis der Benutzer-ausgewählten Kalenderdaten
(Vortag, Stichtag, Folgetag).

Durch die Verwendung von ZonedDateTime mit der Zeitzone
Europe/Vienna werden Tage mit 23 bzw. 25 Stunden
(Sommer-/Winterzeit) korrekt abgebildet.

Fehlende Marktpreise werden explizit als NaN modelliert.


Fehlende Stunden (NaN) werden im Chart nicht verbunden.
Die Zeitreihe wird in zusammenhängende Segmente unterteilt,
wodurch Datenlücken (z. B. durch Sommerzeitumstellung)
visuell als echte Gaps dargestellt werden.