/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.asipsante.ror.nos.converter.ihm;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.support.UISupport;

import fr.asipsante.ror.converter.api.IXMLConverter;
import fr.asipsante.ror.converter.enumeration.NosStatus;
import fr.asipsante.ror.converter.impl.XMLConverter;

/*
 * Construction de la pop voulu pour transformer les fichiers de nomenclatures au format xml. 
 */
public class DialogConverter extends JDialog {
	private static final String CHARSET_ISO = "ISO-8859-1 (recommandé pour Windows)";
	private static final Dimension DIMENSION_DIALOG = new Dimension(600, 250);
	private static final Dimension DIMENSION_ZIP_PATH = new Dimension(350, 30);
	private static final Dimension DIMENSION_COMBO_CHARSET = new Dimension(350, 30);
	private static final Dimension DIMENSION_FOLDER_PATH = new Dimension(350, 30);
	private static final Dimension HORIZONTAL_SPACE = new Dimension(10, 0);
	private static final Dimension ZIP_BOX_SPACE = new Dimension(110, 0);
	private static final Dimension FOLDER_BOX_SPACE = new Dimension(80, 0);
	private static final Dimension VERTICAL_SPACE = new Dimension(0, 10);
	private static final EmptyBorder BORDER_PANEL = new EmptyBorder(10, 10, 10, 10);
	private static final int ITEM_INITIAL = 0;
	private File zipSource;
	private JButton btnConversion = null;
	private Map<String, String> map = new HashMap<>();
	private JComboBox<String> comboCarset;
	private String carset;
	private JTextField zipPath;
	private JPanel panel;
	public DialogConverter() {
		setTitle("Import des Nomenclatures et conversion au format xml");
		setSize(DIMENSION_DIALOG);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setContentPane(buildContentPanel());
	}

	private JPanel buildContentPanel() {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JLabel lblFile = new JLabel("Fichier");
		zipPath = new JTextField("Localisez votre fichier");
		zipPath.setMaximumSize(DIMENSION_ZIP_PATH);
		JButton btnFile = new JButton("Chercher");
		Box zipBox = Box.createHorizontalBox();
		zipBox.add(lblFile);
		zipBox.add(Box.createRigidArea(ZIP_BOX_SPACE));
		zipBox.add(zipPath);
		zipBox.add(Box.createRigidArea(HORIZONTAL_SPACE));
		zipBox.add(btnFile);
		JLabel lblFolder = new JLabel("Dossier cible");
		JTextField folderPath = new JTextField(SoapUI.getSettings().getString("nosFolder", "Value du folder"));
		folderPath.setMaximumSize(new Dimension(DIMENSION_FOLDER_PATH));
		btnFile.addActionListener(new ZipListener());
		Box folderBox = Box.createHorizontalBox();
		folderBox.add(lblFolder);
		folderBox.add(Box.createRigidArea(FOLDER_BOX_SPACE));
		folderBox.add(folderPath);

		JLabel lblCarset = new JLabel("Encodage du texte source");
		String[] listCarset = {CHARSET_ISO,"UTF-8"};
		comboCarset = new JComboBox<>(listCarset);
		comboCarset.setMaximumSize(DIMENSION_COMBO_CHARSET);
		comboCarset.setSelectedItem(ITEM_INITIAL);
		comboCarset.addActionListener(new CarsetListener());
		carset = (String) comboCarset.getSelectedItem();
		Box carsetBox = Box.createHorizontalBox();
		carsetBox.add(lblCarset);
		carsetBox.add(Box.createRigidArea(HORIZONTAL_SPACE));
		carsetBox.add(comboCarset);
		Box conversionBox = Box.createHorizontalBox();
		btnConversion = new JButton("Convertir en XML");
		btnConversion.addActionListener(new ConverterListener());
		conversionBox.add(btnConversion);
		folderBox.setAlignmentX(LEFT_ALIGNMENT);
		carsetBox.setAlignmentX(LEFT_ALIGNMENT);
		zipBox.setAlignmentX(LEFT_ALIGNMENT);
		conversionBox.setAlignmentX(LEFT_ALIGNMENT);
		panel.setBorder(BORDER_PANEL);
		panel.add(zipBox);
		panel.add(Box.createRigidArea(VERTICAL_SPACE));
		panel.add(folderBox);
		panel.add(Box.createRigidArea(VERTICAL_SPACE));
		panel.add(carsetBox);
		panel.add(Box.createRigidArea(VERTICAL_SPACE));
		panel.add(conversionBox);

		return panel;
	}

	class CarsetListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			carset = (String) comboCarset.getSelectedItem();
		}
	}

	class ZipListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers zip", "zip");
			chooser.setFileFilter(filter);
			chooser.showOpenDialog(panel);
			zipSource = chooser.getSelectedFile();
			zipPath.setText(zipSource.getAbsolutePath());
		}

	}

	class ConverterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int fileTransform = 0;
			int fileInvalid = 0;
			int fileAbsent = 0;
			try {
				File targetFile = new File(SoapUI.getSettings().getString("nosFolder", "Value du folder"));
				IXMLConverter converter = new XMLConverter();
				String charset = (String) comboCarset.getSelectedItem();
				if (charset.equals(CHARSET_ISO)) {
					charset = "ISO-8859-1";
				}
				map = converter.convert(zipSource, targetFile, Charset.forName(charset), false);

			} catch (IOException ex) {
				UISupport.showErrorMessage("IOException voir les logs");
			}
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (entry.getValue().equals(NosStatus.TRANSFORM.getValue())) {
					fileTransform++;
				} else if (entry.getValue().equals(NosStatus.INVALID.getValue())) {
					fileInvalid++;
					SoapUI.log.error("Le fichier de nomenclature : " + entry.getValue() + " est invalide");
				} else if (entry.getValue().equals(NosStatus.NULLPOINTER.getValue())) {
					fileAbsent++;
					SoapUI.log.error("Le fichier de nomenclature : " + entry.getValue() + " est introuvable dans le fichier zip d'entrée");
				}

			}
			SoapUI.log.info(fileTransform + "/" + map.size() + " fichiers transformés");
			
			
			UISupport.showInfoMessage(fileTransform + "/" + map.size() + " fichiers transformés\n"
					+ fileInvalid + "/" + map.size() + " fichiers invalides\n"
					+ fileAbsent + "/" + map.size() + " fichiers manquants");
			setVisible(false);
		}

	}

}
