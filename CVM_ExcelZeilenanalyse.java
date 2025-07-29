import java.io.*;
//Importierung der Klasse für Dateioperationen
import java.util.*;
//Importierung der Klasse für Sammlungen
import java.util.stream.Stream;
//Importierung der Klasse für Stream-Operationen
import org.apache.poi.ss.usermodel.*;
//Importierung der Klasse für Excel-Arbeitsblätter
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// Importierung der Klasse für Excel-Arbeitsmappen
import org.apache.poi.util.IOUtils;
// Importierung der Klasse für IO-Operationen

public class CVM_ExcelZeilenanalyse {
    //Methode zur Analyse einer Excel-Datei auf disjunkte Zeilen

    static class CVMSchätzer {
        //Klasse zur Schätzung disjunker Zeilen in einer Excel-Datei
        private final int s;
        //Größe des Puffers, je höher die Größe, desto genauer die Schätzung
        private final Random random;
        //Zufallszahlengenerator zur Generierung von Zufallszahlen für jedes Element
        private final TreeMap<String, Double> Puffer;
        // Erstellung einer TreeMap, um die Elemente und ihre Zufallszahlen zu speichern. Größe richtet sich nach der Größe des Puffers
        private double p;
        // Variable für die Wahrscheinlichkeit, dass ein Element in den Puffer aufgenommen wird

        public CVMSchätzer(int bufferSize) {
            // Konstruktor der Klasse, der die Größe des Puffers initialisiert
            this.s = bufferSize;
            //Setzen der Größe des Puffers
            this.random = new Random();
            //Initialisierung eines neuen Zufallsgenerators für die Erstellung von Zufallszahlen für jedes Element
            this.Puffer = new TreeMap<>();
            //Initialisierung des Puffers als TreeMap, um die Elemente und ihre Zufallszahlen zu speichern
            this.p = 1.0;
            //Setzen der Wahrscheinlichkeit auf 1.0, um sicherzustellen, dass vorerst alle Elemente in den Puffer aufgenommen werden
        }

        public double SchätzeEinzigartig(Stream<String> stream) {
            //Methode zur Schätzung der Anzahl unterschiedlicher Elemente in einem Stream
            Puffer.clear();
            // Löschen des Puffers, um sicherzustellen, dass er leer ist, bevor neue Elemente hinzugefügt werden
            p = 1.0;
            // Setzen der Wahrscheinlichkeit auf 1.0, um sicherzustellen, dass vorerst alle Elemente in den Puffer aufgenommen werden
            stream.forEach(this::verarbeite_Element);
            // Abruf und Verarbeitung jedes Elements im Stream
            return Puffer.size() / p;
            // Rückgabe der geschätzten Anzahl unterschiedlicher Elemente, indem die Größe des Puffers durch die Wahrscheinlichkeit geteilt wird
        }

        private void verarbeite_Element(String a) {
            //Methode zur Verarbeitung eines einzelnen Datenelements im Stream
            Puffer.remove(a);
            // Entfernen des Elements aus dem Puffer, falls es bereits vorhanden ist
            double u = random.nextDouble();
            // Generierung einer Zufallszahl  als Wahrscheinlichkeit für das Element
            if (u > p) return;
            // Wenn die Zufallszahl größer als die aktuelle Wahrscheinlichkeit ist, wird das Element verworfen

            if (Puffer.size() < s) {
                //Prüfung, ob der Puffer noch Platz für weitere Elemente hat wenn die Größe des Puffers kleiner als die maximale Größe ist
                Puffer.put(a, u);// Hinzufügen des Elements und der Zufallszahl zum Puffer
            } else {
                String maxKey = null;//Suche nach dem Element mit der höchsten Zufallszahl im Puffer und Initialisierung der Variable
                double maxU = -1.0;//Vorerst setzen der maximalen Zufallszahl auf -1.0 um zu verhindern, dass ein Fehler auftritt, falls der Puffer leer ist
                for (Map.Entry<String, Double> entry : Puffer.entrySet()) {
                    //Durchlauf über alle Einträge im Puffer
                    if (entry.getValue() > maxU) {
                        //Wenn die Zufallszahl des aktuellen Eintrags größer ist als die bisherige maximale Zufallszahl
                        maxU = entry.getValue();
                        //Setzen der neuen maximalen Zufallszahl auf die Zufallszahl des aktuellen Eintrags
                        maxKey = entry.getKey();
                        //Setzen des Schlüssels des aktuellen Eintrags als Schlüssel mit der höchsten Zufallszahl   
                    }
                }

                if (u > maxU) {
                    //Prüfung, ob die Zufallszahl des aktuellen Elements größer ist als die maximale Zufallszahl im Puffer
                    p = u;
                    //Setzen der Wahrscheinlichkeit auf die Zufallszahl des aktuellen Elements
                } else {// Wenn die Zufallszahl des aktuellen Elements kleiner oder gleich der maximalen Zufallszahl im Puffer ist
                    Puffer.remove(maxKey);
                    //Entfernen des Elements mit der höchsten Zufallszahl aus dem Puffer
                    Puffer.put(a, u);
                    //Hinzufügen des aktuellen Elements und der Zufallszahl zum Puffer
                    p = maxU;
                    //Setzen der Wahrscheinlichkeit auf die maximale Zufallszahl im Puffer
                }
            }
        }
    }

    public static List<List<String>> readExcelRows(String Dateipfad) {
        //Methode zum Einlesen der Zeilen aus einer Excel-Datei
        List<List<String>> Zeilen = new ArrayList<>();
        //Erstellung einer Liste, um die Zeilen der Excel-Datei zu speichern

        org.apache.poi.util.IOUtils.setByteArrayMaxOverride(200_000_000);
        //Setzen der maximalen Größe des Byte-Arrays auf 200 MB, um große Excel-Dateien zu unterstützen

        try (InputStream input = new FileInputStream(Dateipfad);
        //Erstellung eines InputStreams zum Lesen der Excel-Datei
             Workbook workbook = new XSSFWorkbook(input)) {
                //Erstellung eines XSSFWorkbook-Objekts zum Arbeiten mit der Excel-Datei

            Sheet Arbeitsblatt = workbook.getSheetAt(0);
            //Zugriff auf das erste Arbeitsblatt der Excel-Datei

            for (Row aktuelleZeile : Arbeitsblatt) {
                //Durchlauf über alle Zeilen im Arbeitsblatt
                List<String> rowData = new ArrayList<>();
                //Erstellung einer Liste, um die Daten der aktuellen Zeile zu speichern
                for (int i = 0; i < aktuelleZeile.getLastCellNum(); i++) {
                    //Durchlauf über alle Zellen in der aktuellen Zeile
                    Cell aktuelleZelle = aktuelleZeile.getCell(i);
                    //Zugriff auf die Zelle in der aktuellen Zeile und Spalte
                    String value = (aktuelleZelle == null) ? "" : aktuelleZelle.toString().trim();
                    // Umwandlung des Zellinhalts in einen String und Entfernen von Leerzeichen
                    rowData.add(value);
                    // Hinzufügen des Zellinhalts zur Liste der Zeilendaten
                }
                Zeilen.add(rowData);
                // Hinzufügen der Zeilendaten zur Liste der Zeilen
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Einlesen der Excel-Datei:");
            //Fehlerbehandlung, falls beim Einlesen der Excel-Datei ein Fehler auftritt
            e.printStackTrace();
            //Ausgabe des Fehlers auf der Konsole
        }

        return Zeilen;
    }

    public static void main(String[] args) {
        String Dateipfad = "C:\\Users\\marti\\Desktop\\Datensätze\\Auto.xlsx";
//Pfad zur Excel-Datei, die analysiert werden soll
        int Puffergröße = 100;
//Größe des Puffers für die Schätzung der unterschiedlichen Werte, je höher die Größe, desto genauer die Schätzung

        System.out.println(" CVMEstimator – Zeilenweise Analyse auf Abweichungen");
        //Ausgabe einer Überschrift für die Analyse
        System.out.println(" Datei: " + Dateipfad + "\n");
        //Ausgabe des Pfads zur Excel-Datei

        if (!new File(Dateipfad).exists()) {
            //Prüfung, ob die Datei existiert
            System.err.println(" Datei nicht gefunden: " + Dateipfad);
            //Fehlerausgabe, falls die Datei nicht gefunden wurde
            return;
        }

        List<List<String>> excelZeilen = readExcelRows(Dateipfad);
        //Einlesen der Zeilen aus der Excel-Datei
        if (excelZeilen.isEmpty()) {
            //Prüfung, ob die Liste der Zeilen leer ist
            System.err.println(" Keine Daten gefunden.");
            //Fehlerausgabe, falls keine Daten in der Excel-Datei gefunden wurden
            return;
        }

        Runtime Laufzeit = Runtime.getRuntime();
        //Zugriff auf das Runtime-Objekt, um Informationen über die Java-Laufzeitumgebung zu erhalten
        Laufzeit.gc();
        //Aufruf der Garbage Collection, um den Speicher freizugeben, bevor die Analyse beginnt
        long Speicher_vorher = Laufzeit.totalMemory() - Laufzeit.freeMemory();
        //Speichern des aktuellen Speicherverbrauchs vor der Analyse
        long Zeit_vorher = System.nanoTime();
        //Abruf der aktuellen Zeit in Nanosekunden vor der Ausführung des Codes

        int disjunktZeilen = 0;
        //Zähler für die Anzahl der disjunkten Zeilen, die mindestens 2 unterschiedliche Werte enthalten
        CVMSchätzer schätzer = new CVMSchätzer(Puffergröße);

        for (int i = 0; i < excelZeilen.size(); i++) {
            //Durchlauf über alle Zeilen in der Excel-Datei
            List<String> Zeile = excelZeilen.get(i);
            //Abruf der aktuellen Zeile aus der Liste der Zeilen
            double Schätzung = schätzer.SchätzeEinzigartig(Zeile.stream());
            //Schätzung der Anzahl unterschiedlicher Werte in der aktuellen Zeile

            if (Schätzung >= 2.0) {
                //Prüfung, ob die Schätzung mindestens 2 unterschiedliche Werte ergibt
                disjunktZeilen++;
                //Erhöhung des Zählers für disjunkte Zeilen
            }
        }

        long Zeit_nachher = System.nanoTime();
        //Abruf der aktuellen Zeit in Nanosekunden nach der Ausführung des Codes
        long Speicher_nachher = Laufzeit.totalMemory() - Laufzeit.freeMemory();
        //Speichern des aktuellen Speicherverbrauchs nach der Analyse
        long Speicher_verwendet = Speicher_nachher - Speicher_vorher;

        double Laufzeit_ms = (Zeit_nachher - Zeit_vorher) / 1_000_000.0;
        //Berechnung der Gesamtlaufzeit in Millisekunden
        double Speicher_kb = (Speicher_nachher - Speicher_vorher) / 1024.0;
        //Berechnung des gesamten Speicherverbrauchs in Kilobyte

        System.out.println(" Zusammenfassung:");
        //Ausgabe einer Zusammenfassung der Analyse
        System.out.printf(" Disjunkte Zeilen (≥ 2 geschätzte unterschiedliche Werte): %d%n", disjunktZeilen);
        //Ausgabe der Anzahl disjunkter Zeilen
        System.out.printf(" Gesamtlaufzeit: %.2f ms%n", Laufzeit_ms);
        //Ausgabe der Gesamtlaufzeit der Analyse in Millisekunden
        System.out.printf(" Gesamter Speicherverbrauch: %.2f KB%n", Speicher_kb);
        //Ausgabe des gesamten Speicherverbrauchs der Analyse in Kilobyte
    }
}
