Dieses Repository ist Bestandteil einer Bachelorarbeit an der Hochschule Landshut

Autor: Martin Schuldes

Titel der Arbeit: Der CVM-Algorithmus im Praxistest

In diesem Repository wurde der CVM Algorithmus in der verbesserten Version, die am 29.12.2023 von Donald E. Knuth veröffentlicht wurde in Java implementiert  

Seine Ausarbeitungen hierzu können unter https://www-cs-faculty.stanford.edu/~knuth/papers/cvm-note.pdf abgerufen werden

Die erste Datei wurde zur Prüfung  der allgemeinen Funktionsfähigkeit des Codes verwendet. In dieser wurde der Algorithmus wie durch Donald E. Knuth vorgestellt, implementiert. Der Datenstrom stammt in dieser Datei aus einem Array. Es ist gelungen die Funktionsfähigkeit des Algorithmus nachzuweise 

In der zweiten Datei wurde eine Implementierung vorgenommen, mit der es möglich ist, die Anzahl disjunkter Zeilen in einer RTF-Datei abzuschätzen. Zur Generierung der Datei wurde hierbei der ebenfalls veröffentlichte Kundendatenbankgenerator verwendet

In der letzten Datei wurde eine Implementierung vorgenommen, die das zeilenweise Einlesen einer Excel-Datei ermöglicht. Hierbei soll die Menge disjunkter Zeilen abgeschätzt werden. Die Testdatensätze stammen hierbei vom renommierten Datenbereitsteller Kaagle.com

Zum Einlesen einer RTF-Datei ist es notwendig die Bibliothek apache.poi einzubinden. Hierzu ist diese auf der Herstellerhomepage herunterzuladen und den Umgebungsvariablen des Betriebssystems hinzuzufügen

Danach kann die Bibliothek mittels Maven in ein beliebiges Projekt eingebunden werden

Letztes Uploade 29.07.2025
