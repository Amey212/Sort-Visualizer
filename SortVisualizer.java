import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class SortVisualizer extends JFrame {
    private int[] array;
    private final SortPanel sortPanel;
    private JComboBox<String> algorithmBox;
    private final JButton startButton;
    private final JButton resetButton;
    private volatile boolean isSorting = false;
    private volatile int currentIndex = -1;
    private volatile int comparingIndex = -1;

    public SortVisualizer() {
        setTitle("Sorting Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        sortPanel = new SortPanel();
        add(sortPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        algorithmBox = new JComboBox<>(new String[]{"Bubble Sort", "Selection Sort"});
        startButton = new JButton("Start");
        resetButton = new JButton("Reset");

        controlPanel.add(algorithmBox);
        controlPanel.add(startButton);
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> {
            if (!isSorting) {
                String selectedAlgorithm = (String) algorithmBox.getSelectedItem();
                startSorting(selectedAlgorithm);
            }
        });

        resetButton.addActionListener(e -> initializeArray());

        initializeArray();
        setVisible(true);
    }

    private void initializeArray() {
        array = new int[20];
        Random rand = new Random();

        // Ensure the height is valid before using it
        int panelHeight = Math.max(sortPanel.getHeight(), 200); // Set a minimum panel height
        int maxValue = panelHeight - 20; // Upper bound for random numbers

        for (int i = 0; i < array.length; i++) {
            if (maxValue > 0) {
                array[i] = rand.nextInt(maxValue) + 10; // Use a higher minimum value to ensure bars are visible
            } else {
                array[i] = 10; // Default value if height is too small
            }
        }
        repaint();
    }

    private void startSorting(String algorithm) {
        isSorting = true;
        startButton.setEnabled(false);
        resetButton.setEnabled(false);

        new Thread(() -> {
            switch (algorithm) {
                case "Bubble Sort" -> bubbleSort();
                case "Selection Sort" -> selectionSort();
            }
            isSorting = false;
            currentIndex = -1;
            comparingIndex = -1;
            SwingUtilities.invokeLater(() -> {
                startButton.setEnabled(true);
                resetButton.setEnabled(true);
                repaint();
            });
        }).start();
    }

    private void bubbleSort() {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                currentIndex = j;
                comparingIndex = j + 1;

                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }

                try {
                    Thread.sleep(50); // Add a slight delay for smoother visualization
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                SwingUtilities.invokeLater(() -> repaint());
            }
        }
    }

    private void selectionSort() {
        for (int i = 0; i < array.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                currentIndex = j;
                comparingIndex = minIndex;

                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }

                try {
                    Thread.sleep(50); // Add a slight delay for smoother visualization
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                SwingUtilities.invokeLater(() -> repaint());
            }
            int temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;

            SwingUtilities.invokeLater(() -> repaint());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class SortPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (array == null) return;

            int barWidth = Math.max(1, getWidth() / array.length);
            for (int i = 0; i < array.length; i++) {
                int barHeight = array[i] * 2; // Scale the height by a factor of 2
                int x = i * barWidth;
                int y = getHeight() - barHeight;

                if (i == currentIndex || i == comparingIndex) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLUE);
                }
                g.fillRect(x, y, barWidth - 1, barHeight);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SortVisualizer::new);
    }
}
