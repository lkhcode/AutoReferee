/*
 * Copyright (c) 2009 - 2018, DHBW Mannheim - TIGERs Mannheim
 */
package edu.tigers.autoref.view.main;

import java.awt.*;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.tigers.autoreferee.engine.EAutoRefMode;
import edu.tigers.sumatra.components.BasePanel;


public class StartStopPanel extends BasePanel<StartStopPanel.IStartStopPanelObserver>
{
	private final Map<EAutoRefMode, ButtonModel> autoRefModeModels = new EnumMap<>(EAutoRefMode.class);
	
	private final ButtonGroup group = new ButtonGroup();
	
	
	public StartStopPanel()
	{
		setupUI();
		createButtons();
		setAutoRefMode(EAutoRefMode.OFF);
	}
	
	private void setupUI()
	{
		setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(5, 10, 5, 10));
	}
	
	private void createButtons()
	{
		JRadioButton off = createStyledButton("🔴 关闭", "完全禁用自动裁判功能");
		off.addActionListener(e -> autoRefModeChanged(EAutoRefMode.OFF));
		group.add(off);
		add(off);
		
		JRadioButton passive = createStyledButton("🟡 被动模式", "检测违规但不发送命令");
		passive.addActionListener(e -> autoRefModeChanged(EAutoRefMode.PASSIVE));
		group.add(passive);
		add(passive);
		
		JRadioButton active = createStyledButton("🟢 主动模式", "检测违规并自动发送命令");
		active.addActionListener(e -> autoRefModeChanged(EAutoRefMode.ACTIVE));
		group.add(active);
		add(active);
		
		autoRefModeModels.put(EAutoRefMode.OFF, off.getModel());
		autoRefModeModels.put(EAutoRefMode.PASSIVE, passive.getModel());
		autoRefModeModels.put(EAutoRefMode.ACTIVE, active.getModel());
	}
	
	private JRadioButton createStyledButton(String text, String tooltip)
	{
		JRadioButton button = new JRadioButton(text);
		button.setToolTipText(tooltip);
		button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		button.setFocusPainted(false);
		button.setBackground(Color.WHITE);
		button.setBorder(new EmptyBorder(5, 10, 5, 10));
		
		// 添加鼠标悬停效果
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if (!button.isSelected()) {
					button.setBackground(new Color(230, 240, 255));
				}
			}
			
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				if (!button.isSelected()) {
					button.setBackground(Color.WHITE);
				}
			}
		});
		
		return button;
	}
	
	
	private void autoRefModeChanged(EAutoRefMode mode)
	{
		informObserver(o -> o.changeMode(mode));
	}
	
	
	public void setAutoRefMode(EAutoRefMode mode)
	{
		group.setSelected(autoRefModeModels.get(mode), true);
	}
	
	
	@Override
	public void setEnabled(final boolean enabled)
	{
		super.setEnabled(enabled);
		Arrays.asList(getComponents()).forEach(c -> c.setEnabled(enabled));
	}
	
	public interface IStartStopPanelObserver
	{
		void changeMode(final EAutoRefMode mode);
	}
}
