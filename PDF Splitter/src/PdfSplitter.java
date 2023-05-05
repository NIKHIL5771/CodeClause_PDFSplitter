import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfSplitter extends JFrame {
    private JPanel panel;
    private JButton selectFileBtn, splitBtn, savePathBtn;
    private JLabel fileLabel, rangeLabel, savePathLabel;
    private JTextField rangeField, savePathField;
    private File selectedFile, savePath;
    private JFileChooser savePathChooser;

    public PdfSplitter() {
        panel = new JPanel();
        selectFileBtn = new JButton("Select PDF File");
        splitBtn = new JButton("Split PDF");
        savePathBtn = new JButton("Select Save Path");
        fileLabel = new JLabel("No file selected");
        rangeLabel = new JLabel("Enter page range (e.g. 1-5):");
        savePathLabel = new JLabel("Select Save Path:");
        rangeField = new JTextField(10);
        savePathField = new JTextField(10);

        selectFileBtn.addActionListener(e -> selectFile());
        splitBtn.addActionListener(e -> splitPdf());
        savePathBtn.addActionListener(e -> selectSavePath());

        panel.add(selectFileBtn);
        panel.add(fileLabel);
        panel.add(rangeLabel);
        panel.add(rangeField);
        panel.add(savePathLabel);
        panel.add(savePathField);
        panel.add(savePathBtn);
        panel.add(splitBtn);

        add(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileLabel.setText(selectedFile.getName());
        }
    }

    private void selectSavePath() {
        if (savePathChooser == null) {
            savePathChooser = new JFileChooser();
            savePathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        int result = savePathChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            savePath = savePathChooser.getSelectedFile();
            savePathField.setText(savePath.getAbsolutePath());
        }
    }

   private void splitPdf() {
    if (selectedFile == null) {
        JOptionPane.showMessageDialog(this, "Please select a PDF file first");
        return;
    }
    String[] range = rangeField.getText().split("-");
    if (range.length != 2) {
        JOptionPane.showMessageDialog(this, "Invalid page range. Please enter a range in the format 'start-end'");
        return;
    }
    int startPage = Integer.parseInt(range[0]);
    int endPage = Integer.parseInt(range[1]);
    if (savePath == null) {
        JOptionPane.showMessageDialog(this, "Please select a save path first");
        return;
    }
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Split PDFs");
    fileChooser.setCurrentDirectory(savePath);
    int result = fileChooser.showSaveDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        try {
            PDDocument document = Loader.loadPDF(selectedFile);
            Splitter splitter = new Splitter();
            splitter.setStartPage(startPage);
            splitter.setEndPage(endPage);
            splitter.setSplitAtPage(endPage);
            splitter.setSplitAtPage(startPage);
            splitter.setSplitAtPage(document.getNumberOfPages() + 1);
            splitter.setSplitAtPage(startPage - 1);
            splitter.setSplitAtPage(endPage + 1);
            int pageCounter = startPage;
            for (PDDocument page : splitter.split(document)) {
                String fileName = fileChooser.getSelectedFile().getName().replace(".pdf", "") + "_" + pageCounter + ".pdf";
                File saveFile = new File(fileChooser.getSelectedFile().getParentFile(), fileName);
                page.save(saveFile);
                page.close();
                pageCounter++;
            }
            document.close();
            JOptionPane.showMessageDialog(this, "PDF file has been split and saved successfully");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while splitting the PDF file");
        }
    }


    JOptionPane.showMessageDialog(this, "PDF file has been saved successfully");

}



    public static void main(String[] args) {
        PdfSplitter pdfSplitter = new PdfSplitter();
    }
}


   
