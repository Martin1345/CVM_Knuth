import java.io.*;//Improtierung der Klasse für die Ein- und Ausgabe von Daten
import java.util.*;// Importierung der Klassen für die Arbeit mit Sammlungen
import java.util.stream.Stream;// Importierung der Klasse für die Arbeit mit Streams
import javax.swing.text.BadLocationException;// Importierung der Klasse für die Behandlung von Fehlern bei der Textverarbeitung
import javax.swing.text.DefaultStyledDocument;// Importierung der Klasse für die Arbeit mit stilisierten Dokumenten
import javax.swing.text.rtf.RTFEditorKit;// Importierung der Klasse für die Arbeit mit RTF-Dokumenten

public class CVMEstimator {

    private final int s;
    // Größe des Puffers je größer der Puffer, desto genauer die Schätzung
    private final Random random;
    // Zufallsgeneratorinstanz, um Zufallszahlen je Element zu generieren
    private final TreeMap<String, Double> buffer;
    //Erschaffung eines Puffers, der die Elemente und ihre Zufallszahlen speichert
    private double p;
    // Wahrscheinlichkeit, dass ein Element in den Puffer aufgenommen wird

    public CVMEstimator(int bufferSize) {
        // Konstruktor für die Initialisierung des CVM-Estimators
        this.s = bufferSize;
        //Setzen der Puffergröße
        this.random = new Random(); 
        // Erschaffung eines neuen Zufallsgenerators
        this.buffer = new TreeMap<>();
         // Erschaffung eines neuen TreeMap-Puffers, der die Elemente und ihre Zufallszahlen speichert
        this.p = 1.0;
    }

    public double estimateDistinct(Stream<String> stream) {// Methode zur Schätzung der Anzahl der unterschiedlichen Elemente im Stream
        stream.forEach(this::processElement);
        // Verarbeitung jedes Elements im Stream
        return buffer.size() / p;
        // Rückgabe der Schätzung der Anzahl der unterschiedlichen Elemente geteilt durch die Wahrscheinlichkeit
    }

    private void processElement(String a) {
        // Methode zur Verarbeitung eines einzelnen Elements mit Überprüfung der Wahrscheinlichkeit
        buffer.remove(a);
        // Entfernen des Elements aus dem Puffer, falls es bereits vorhanden ist
        double u = random.nextDouble();
        // Generierung einer Zufallszahl zwischen 0 und 1 für das Element
        if (u > p) return;
        // Wenn die Zufallszahl größer als die Wahrscheinlichkeit ist, wird das Element nicht weiter verarbeitet

        if (buffer.size() < s) {
            // Wenn der Puffer noch nicht voll ist, wird das Element hinzugefügt
            buffer.put(a, u);
            // Hinzufügen des Elements und der Zufallszahl zum Puffer
        } else {
            // Wenn der Puffer voll ist, wird das Element mit der höchsten Zufallszahl entfernt
            String maxKey = null;// Variable zum Speichern des Schlüssels des Elements mit der höchsten Zufallszahl
            double maxU = -1.0;// Variable zum Speichern der höchsten Zufallszahl
            for (Map.Entry<String, Double> entry : buffer.entrySet()) {// Iteration über die Einträge im Puffer
                if (entry.getValue() > maxU) {// Wenn die Zufallszahl des aktuellen Eintrags größer ist als die bisher höchste
                    maxU = entry.getValue();// Aktualisierung der höchsten Zufallszahl
                    maxKey = entry.getKey();// Aktualisierung des Schlüssels des Eintrags mit der höchsten Zufallszahl
                }
            }

            if (u > maxU) {
                // Wenn die neue Zufallszahl größer ist als die höchste Zufallszahl im Puffer
                p = u;
                // Aktualisierung der Wahrscheinlichkeit auf die neue Zufallszahl
            } else {
                // Wenn die neue Zufallszahl kleiner oder gleich der höchsten Zufallszahl ist
                buffer.remove(maxKey);
                // Entfernen des Eintrags mit der höchsten Zufallszahl aus dem Puffer
                buffer.put(a, u);
                // Hinzufügen des neuen Elements und der Zufallszahl zum Puffer
                p = maxU;
                // Aktualisierung der Wahrscheinlichkeit auf die höchste Zufallszahl im Puffe
            }
        }
    }

    public static List<String> readRTFLines(String filePath) {
        // Methode zum Einlesen von Zeilen aus einer RTF-Datei
        List<String> lines = new ArrayList<>();
        // Erschaffung einer Liste, um die Zeilen zu speichern
        try (InputStream input = new FileInputStream(filePath)) {
            // Öffnen der RTF-Datei zum Lesen
            RTFEditorKit parser = new RTFEditorKit();
            // Erschaffung eines RTF-Editors zum Parsen der Datei
            DefaultStyledDocument document = new DefaultStyledDocument();
            // Erschaffung eines stilisierten Dokuments zum Speichern des Inhalts
            parser.read(input, document, 0);
            // Lesen des Inhalts der RTF-Datei in das Dokument

            String text = document.getText(0, document.getLength());
            // Extrahieren des Textes aus dem Dokument
            BufferedReader reader = new BufferedReader(new StringReader(text));
            // Erschaffung eines Puffers zum Lesen des Textes zeilenweise
            String line;
            // Initialisierung der Zeilenvariable
            while ((line = reader.readLine()) != null) {
                // Lesen der Zeilen aus dem Puffer
                line = line.trim();
                // Entfernen von führenden und nachfolgenden Leerzeichen
                if (!line.isEmpty()) {
                    // Überprüfen, ob die Zeile nicht leer ist
                    lines.add(line);
                    // Hinzufügen der Zeile zur Liste
                }
            }
        } catch (IOException | BadLocationException e) {
            System.err.println("Fehler beim Einlesen der Datei:");
            // Fehlerbehandlung bei IO- oder Textverarbeitungsfehlern
            e.printStackTrace();
            // Weitere Fehlerbehandlung kann hier hinzugefügt werden
        }
        return lines;
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\marti\\Desktop\\Datensätze\\Hotel.rtf";

        int bufferSize = 3000; 
        // Beispielwert, anpassen je nach Bedarf je Größer der Puffer, desto genauer die Schätzung
        int runs = 1000;
        // Anzahl der Durchläufe für die Schätzung

        List<String> streamData = readRTFLines(filePath);
        // Einlesen der Zeilen aus der RTF-Datei
        int totalLines = streamData.size();
        // Gesamtanzahl der Zeilen im Stream
        if (totalLines == 0) {
            // Überprüfen, ob keine Zeilen eingelesen wurden
            System.out.println("Keine Zeilen gefunden.");
            // Ausgabe einer Fehlermeldung und Abbruch des Programms
            return;
            // Beenden des Programms, wenn keine Zeilen vorhanden sind
        }

        double[] estimates = new double[runs];
        // Erschaffung eines Arrays zur Speicherung der Schätzungen
        List<Long> runtimes = new ArrayList<>();
        // Erschaffung einer Liste zur Speicherung der Laufzeiten
        List<Long> memoryUsages = new ArrayList<>();
        // Erschaffung einer Liste zur Speicherung der Speicherverwendungen

        for (int i = 0; i < runs; i++) {
            Runtime runtime = Runtime.getRuntime();
            // Erschaffung einer Instanz der Runtime-Klasse zur Überwachung der Systemressourcen
            runtime.gc();
            // Anfordern der Garbage Collection, um den Speicherverbrauch zu optimieren

            long beforeMem = runtime.totalMemory() - runtime.freeMemory();
            // Abruf des aktuellen Speicherverbrauchs vor der Programmausführung
            long startTime = System.nanoTime();
            // Abruf der aktuellen Zeit in Nanosekunden für die Messung der Laufzeit vor der Programmausführung

            CVMEstimator estimator = new CVMEstimator(bufferSize);
            // Erschaffung einer Instanz des CVM-Estimators mit der angegebenen Puffergröße
            double estimate = estimator.estimateDistinct(streamData.stream());
            // Schätzung der Anzahl der unterschiedlichen Elemente im Stream

            long endTime = System.nanoTime();
            // Abruf der aktuellen Zeit in Nanosekunden nach der Programmausführung
            long afterMem = runtime.totalMemory() - runtime.freeMemory();
            // Abruf des aktuellen Speicherverbrauchs nach der Programmausführung

            long usedMem = afterMem - beforeMem;
            // Berechnung des tatsächlich verwendeten Speichers in Bytes
            long durationNs = endTime - startTime;
// Berechnung der Laufzeit in Nanosekunden
            estimates[i] = estimate;
            // Speicherung der Schätzung im Array
            runtimes.add(durationNs);
            // Hinzufügen der Laufzeit zur Liste
            memoryUsages.add(usedMem);
            // Hinzufügen des Speicherverbrauchs zur Liste

            if ((i + 1) % 100 == 0 || i == 0 || i == runs - 1) {
                System.out.printf("Durchlauf %d: geschätzt = %.2f, Zeit = %.2f ms, Speicher = %.2f KB%n",
                // Ausgabe der Ergebnisse für jeden 100. Durchlauf,  sowie den ersten und den letzten
                        i + 1, estimate, durationNs / 1_000_000.0, usedMem / 1024.0);
                        // Ausgabe der Schätzung, Laufzeit in Millisekunden und Speicherverbrauch in Kilobyte
            }
        }

        double avgEstimate = Arrays.stream(estimates).average().orElse(0.0);
        // Berechnung des Durchschnitts der Schätzungen
        double[] sorted = Arrays.stream(estimates).sorted().toArray();// Sortierung der Schätzungen in aufsteigender Reihenfolge
        double medianEstimate = (runs % 2 == 0)
                ? (sorted[runs / 2 - 1] + sorted[runs / 2]) / 2.0
                : sorted[runs / 2];
                // Berechnung des Medians der Schätzungen

        int avgDeviationPercent = (int) Math.round(
            // Berechnung der durchschnittlichen Abweichung in Prozent
                Arrays.stream(estimates)
                // Berechnung der Abweichung jedes Schätzwerts von der tatsächlichen Anzahl der Zeilen
                        .map(e -> Math.abs(e - totalLines) / totalLines * 100.0)
                        .filter(e -> !Double.isNaN(e))
                        // Filtern von NaN-Werten (nicht anwendbar in diesem Fall, aber sicherheitshalber)
                        .average()
                        // Berechnung des Durchschnitts der Abweichungen
                        .orElse(0.0)
    
        );

        double avgTimeMs = runtimes.stream().mapToLong(Long::longValue).average().orElse(0) / 1_000_000.0;// Berechnung der durchschnittlichen Laufzeit in Millisekunden
        double avgMemoryKb = memoryUsages.stream().mapToLong(Long::longValue).average().orElse(0) / 1024.0;// Berechnung des durchschnittlichen Speicherverbrauchs in Kilobyte

        System.out.println(" Zusammenfassung:");// Ankündigung der Zusammenfassung der Ergebnisse
        System.out.printf(" Tatsächliche Anzahl Zeilen: %d%n", totalLines);//Ausgabe der Anzahl an eigeksensen Zeilen
        System.out.printf(" Durchschnittlich geschätzte disjunkte Zeilen: %.2f%n", avgEstimate);// Ausgabe des Durchschnitts der geschätzten disjunkten Zeilen
        System.out.printf(" Median geschätzte disjunkte Zeilen: %.2f%n", medianEstimate);// Ausgabe des Medians der geschätzten disjunkten Zeilen
        System.out.printf(" Durchschnittliche Abweichung: %d %%\n", avgDeviationPercent);// Ausgabe der durchschnittlichen Abweichung in Prozent
        System.out.printf(" Durchschnittliche Laufzeit: %.2f ms%n", avgTimeMs);// Ausgabe der durchschnittlichen Laufzeit in Millisekunden
        System.out.printf(" Durchschnittlicher Speicherverbrauch: %.2f KB%n", avgMemoryKb);// Ausgabe des durchschnittlichen Speicherverbrauchs in Kilobyte
    }
}
