/* Include.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.Execution;//for javadoc
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;//for javadoc
import org.zkoss.zk.ui.util.Clients;//for javadoc
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.mesg.MZul;//for javadoc

/**
 * Includes the result generated by any servlet, not limited to a ZUML page.
 * 
 * <p>
 * Non-XUL extension.
 * 
 * <p>Since 3.6.2, there are three modes: auto, instant and defer (default).
 * The behavior prior to 3.6.2 is the same as the defer mode.
 * To be fully backward compatible, the default mode is <code>defer</code>.
 * However, we recommend you change it by specifying a library variable named
 * <code>org.zkoss.zul.include.mode</code> to be <code>auto</code>,
 * since the auto mode is more intuitive and easier to handle.
 *
 * <h3>The instant mode</h3>
 *
 * <p>In the instant mode, the page to be included are loaded 'instantly'
 * with {@link Execution#createComponents} when {@link #afterCompose}
 * is called. Furthermore, the components are created as the child components
 * of this include component (like a macro component).
 *
 * <p>Notices:
 * <ul>
 * <li>The instant mode supports only ZUML pages.</li>
 * <li>The isntance mode doesn't support {@link #setProgressing} nor
 * {@link #setLocalized}</li>
 * </ul>
 *
 * <h3>The defer mode (default)</h3>
 *
 * <p>In the defer mode (the only mode supported by ZK prior to 3.6.2),
 * the page is included by servlet container (the <code>include</code> method
 * of <code>javax.servlet.RequestDispatcher</code>) in the render phase
 * (i.e., after all components are created). The page can be any
 * servlet; not limited to a ZUML page.
 *
 * <p>If it is eventually another ZUML page, a ZK page ({@link org.zkoss.zk.ui.Page})
 * is created and added to the current desktop.
 * You can access them only via inter-page API (see{@link org.zkoss.zk.ui.Path}).
 *
 * <h3>The auto mode</h3>
 *
 * <p>In the auto mode, the include component decides the mode based on
 * the name specified in the src property ({@link #setSrc}).
 * If <code>src</code> is ended with the extension named <code>.zul</code>
 * or <code>.zhtml</code>, the <code>instant</code> mode is assumed.
 * Otherwise, the <code>defer</code> mode is assumed.
 *
 * <p>Notice that invoking {@link #setProgressing} or {@link #setLocalized}
 * with true will imply the <code>defer</code> mode (if the mode is <code>auto</code>).
 *
 * <h3>Passing Parameters</h3>
 *
 * <p>There are two ways to pass parameters to the included page:
 * <p>First, since ZK 3.0.4,
 * you can use {@link #setDynamicProperty}, or, in ZUL,
 * <pre><code>&lt;include src="/WEB-INF/mypage" arg="something"/&gt;</code></pre>
 *
 * <p>Second, you can use the query string:
 * <pre><code>&lt;include src="/WEB-INF/mypage?arg=something"/&gt;</code></pre>
 *
 * <p>With the query string, you can pass only the String values.
 * and the parameter can be accessed by {@link Execution#getParameter}
 * or javax.servlet.ServletRequest's getParameter.
 * Or, you can access it with the param variable in EL expressions.
 *
 * <p>On the other hand, the dynamic properties ({@link #setDynamicProperty})
 * are passed to the included page thru the request's attributes
 * You can pass any type of objects you want.
 * In the included page, you can access them by use of
 * {@link Execution#getAttribute} or javax.servlet.ServletRequest's
 * getAttribute. Or, you can access with the requestScope variable
 * in EL expressions.
 *
 * <h3>Macro Component versus {@link Include}</h3>
 *
 * If the include component is in the instant mode, it is almost the same as
 * a macro component. On the other hand, if in the pag mode, they are different:
 * <ol>
 * <li>{@link Include} (in defer mode) could include anything include ZUML,
 * JSP or any other
 * servlet, while a macro component could embed only a ZUML page.</li>
 * <li>If {@link Include} (in defer mode) includes a ZUML page, a
 * {@link org.zkoss.zk.ui.Page} instance is created which is owned
 * by {@link Include}. On the other hand, a macro component makes
 * the created components as the direct children -- i.e.,
 * you can browse them with {@link org.zkoss.zk.ui.Component#getChildren}.</li>
 * <li>{@link Include} (in defer mode) creates components in the Rendering phase,
 * while a macro component creates components in {@link org.zkoss.zk.ui.HtmlMacroComponent#afterCompose}.</li>
 * <li>{@link Include#invalidate} (in defer mode) will cause it to re-include
 * the page (and then recreate the page if it includes a ZUML page).
 * However, {@link org.zkoss.zk.ui.HtmlMacroComponent#invalidate} just causes it to redraw
 * and update the content at the client -- like any other component does.
 To re-create, you have to invoke {@link org.zkoss.zk.ui.HtmlMacroComponent#recreate}.</li>
 * </ol>
 *
 * <p>In additions to macro and {@link Include}, you can use the fulfill
 * attribute as follows:
 * <code>&lt;div fulfill="=/my/foo.zul"&gt;...&lt;/div&gt;
 * 
 * @author tomyeh
 * @see Iframe
 * @since 3.5.2
 */
public interface Include extends org.zkoss.zul.impl.api.XulElement,
DynamicPropertied, AfterCompose, IdSpace {

	/**
	 * Sets whether to show the {@link MZul#PLEASE_WAIT} message before a long
	 * operation. This implementation will automatically use an echo event like
	 * {@link Events#echoEvent(String, org.zkoss.zk.ui.Component, String)} to
	 * suspend the including progress before using the
	 * {@link Clients#showBusy(String)} method to show the
	 * {@link MZul#PLEASE_WAIT} message at client side.
	 * 
	 * <p>
	 * Default: false.
	 * 
	 */
	public void setProgressing(boolean progressing);

	/**
	 * Returns whether to show the {@link MZul#PLEASE_WAIT} message before a
	 * long operation.
	 * <p>
	 * Default: false.
	 * 
	 */
	public boolean getProgressing();

	/**
	 * Returns the src.
	 * <p>
	 * Default: null.
	 */
	public String getSrc();

	/**
	 * Sets the src.
	 * 
	 * <p>
	 * If src is changed, the whole component is invalidate. Thus, you want to
	 * smart-update, you have to override this method.
	 * 
	 * @param src
	 *            the source URI. If null or empty, nothing is included. You can
	 *            specify the source URI with the query string and they will
	 *            become a parameter that can be accessed by use of
	 *            {@link Execution#getParameter} or
	 *            javax.servlet.ServletRequest's getParameter. For example, if
	 *            "/a.zul?b=c" is specified, you can access the parameter with
	 *            ${param.b} in a.zul.
	 * @see #setDynamicProperty
	 */
	public void setSrc(String src);

	/** Returns whether the source depends on the current Locale.
	 * If true, it will search xxx_en_US.yyy, xxx_en.yyy and xxx.yyy
	 * for the proper content, where src is assumed to be xxx.yyy.
	 *
	 * <p>Default: false;
	 * @since 3.6.2
	 */
	public boolean isLocalized();
	/** Sets whether the source depends on the current Locale.
	 * @since 3.6.2
	 */
	public void setLocalized(boolean localized);

	/** Returns the inclusion mode.
	 * There are three modes: auto, instant and defer.
	 * The behavior prior to 3.6.2 is the same as the defer mode.
	 * <p>It is recommended to use the auto mode if possible
	 * The reason to have <code>defer</code> as the default is to
	 * be backward compatible.
	 * <p>Default: defer.
	 * @since 3.6.2
	 */
	public String getMode();
	/** Sets the inclusion mode.
	 * @param mode the inclusion mode: auto, instant or defer.
	 * @since 3.6.2
	 */
	public void setMode(String mode) throws WrongValueException;

	/** Removes all dynamic properties.
	 * @since 5.0.1
	 */
	public void clearDynamicProperties();
}
