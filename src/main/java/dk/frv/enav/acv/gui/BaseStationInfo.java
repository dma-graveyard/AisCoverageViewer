package dk.frv.enav.acv.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.project.ProjectHandler;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

public class BaseStationInfo extends OverlayBox implements ActionListener {
	private JLabel bsmmsi;
	private JLabel totalMessages;
	private JLabel totalShips;
	private JCheckBox checkBox;
	private BaseStation basestation;
	public BaseStationInfo(Component invoker, int width, int height) {
		super(width, height);
		
		bsmmsi = new JLabel("bsmmsi");
		bsmmsi.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel lblNewLabel = new JLabel("Total messages");
		
		JLabel lblTotalShips = new JLabel("Total ships");
		
		totalMessages = new JLabel("New label");
		
		totalShips = new JLabel("New label");
		
		JLabel lblVisible = new JLabel("Visible");
		
		checkBox = new JCheckBox("");
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(bsmmsi)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel)
								.addComponent(lblTotalShips)
								.addComponent(lblVisible))
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(totalShips)
										.addComponent(totalMessages)))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(1)
									.addComponent(checkBox)))))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(bsmmsi)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(totalMessages))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTotalShips)
						.addComponent(totalShips))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblVisible))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(2)
							.addComponent(checkBox)))
					.addContainerGap(12, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		
		//listeners
		checkBox.addActionListener(this);
	}

	public void setBaseStation(BaseStation basestation) {
		this.basestation = basestation;
		this.bsmmsi.setText(basestation.identifier+"");
		this.totalMessages.setText(basestation.messageCount+"");
		this.totalShips.setText(basestation.ships.size()+"");
		checkBox.setSelected(basestation.isVisible());
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == checkBox) {
			ProjectHandler.getInstance().getProject().getCoverageCalculator().getBaseStationHandler().setVisible(basestation.identifier, checkBox.isSelected());
		}
		
	}
}
