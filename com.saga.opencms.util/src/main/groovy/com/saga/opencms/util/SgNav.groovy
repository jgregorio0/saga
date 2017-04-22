package com.saga.opencms.util

import org.opencms.file.CmsObject
import org.opencms.flex.CmsFlexController
import org.opencms.jsp.CmsJspNavBuilder
import org.opencms.jsp.CmsJspNavElement

import javax.servlet.http.HttpServletRequest

public class SgNav {

	private CmsJspNavBuilder navigationBuilder;

	public CmsNavUtil(HttpServletRequest request) {
		CmsFlexController controller = CmsFlexController.getController(request);
		CmsObject cmso = controller.getCmsObject();
		this.navigationBuilder = new CmsJspNavBuilder(cmso);
	}

	/**
	 * Devuelve una lista de elementos de navegacion teniendo en cuenta la carpeta
	 * como inicio y el nivel final donde terminar la navegacion
	 * @param folderPath
	 * @param endLevelStr
	 * @return
	 */
	public  List<NavElem> generateNavigation (
			String folderPath, String endLevelStr) {
		// initialize OpenCms access objects
		int currentLevel = navigationBuilder
				.getNavigationForResource(folderPath).getNavTreeLevel();

		int endLevel = endLevelStr == null ?
				Integer.MIN_VALUE : currentLevel + Integer.parseInt(endLevelStr);
		List<CmsJspNavElement> siteNavigation = navigationBuilder
				.getSiteNavigation(folderPath, endLevel);
		List<NavElem> padres = createNavigation(siteNavigation);
		return padres;
	}

	/**
	 * Devuelve un elemento de navegacion dado el path por parametro
	 * @param sitePath
	 * @return
	 */
	public NavElem getNavigationElement (String sitePath){
		CmsJspNavElement cmsNavElem =
				navigationBuilder.getNavigationForResource(sitePath);
		return new NavElem(cmsNavElem);
	}

	/**
	 * Transforma la lista de navegacion en una lista de elementos padre que contienen
	 * a su vez listas de elementos hijo
	 * @param items
	 * @return
	 */
	private List<NavElem> createNavigation(
			List<CmsJspNavElement> items) {

		List<NavElem> fathersNav = new ArrayList<NavElem>();
		List<NavElem> previousBrotherList = new ArrayList<NavElem>();
		NavElem previous = null;
		NavElem previousBrother = null;
		NavElem current;
		NavElem father;

		int currentLevel;
		int previousLevel;
		int previousBrotherLevel;
		for (CmsJspNavElement item : items) {
			current = new NavElem(item);

			// Inicializamos incluyendo el primer elemento a la navegacion
			if (previous == null) {
				fathersNav.add(current);
			} else {
				currentLevel = item.getNavTreeLevel();
				previousLevel = previous.getElement().getNavTreeLevel();

				// Si es hermano del anterior
				if (previousLevel == currentLevel) {
					father = previous.getFather();

					// Si son hermanos y el elemento anterior tiene padre
					// 1- le agregamos el padre al elemento actual
					// 2- incluimos como hijo del padre al elemento actual
					if (father != null) {
						current.setFather(father);
						father.addSon(current);
					} else {

						// Si son hermanos pero el elemento anterior NO tiene padre
						// 1- incluimos el elemento como padre
						fathersNav.add(current);
					}
				}

				// Si es hijo del anterior
				// 1- agregamos el anterior a la lista de ultimos padres
				// 2- agregamos el anterior como padre del actual y
				// 3- agregamos al padre anterior el actual como hijo
				else if (previousLevel < currentLevel) {
					previousBrotherList.add(previous);
					current.setFather(previous);
					previous.addSon(current);
				}

				// Si no es hermano ni hijo del actual, es hermano del nivel anterior
				// 1- Cargamos el hermano anterior
				// 2- Si existe hermano anterior cargamos el padre
				// 3- Si tiene padre lo agregamos al actual y agregamos el actual como hijo
				// 4- Si no tiene padre agregamos el actual como padre

				else {
					previousBrotherLevel = Integer.MAX_VALUE;
					while (previousBrotherLevel > currentLevel){
						previousBrother = previousBrotherList
								.remove(previousBrotherList.size() - 1);
						previousBrotherLevel = previousBrother
								.getElement().getNavTreeLevel();
					}
					if (previousBrother != null) {
						father = previousBrother.getFather();
						if (father != null) {
							current.setFather(father);
							father.addSon(current);
						} else {
							fathersNav.add(current);
						}
					}
				}
			}
			previous = current;
		}
		return fathersNav;
	}

	public class NavElem {

		private CmsJspNavElement element;
		private NavElem father;
		private List<NavElem> sons;
		private boolean hasSons;

		private String href;
		private String title;

		public NavElem(CmsJspNavElement element) {
			setElement(element);
			setSons(new ArrayList<NavElem>());
			hasSons = false;
		}

		public CmsJspNavElement getElement() {
			return element;
		}

		public void setElement(CmsJspNavElement element) {
			this.element = element;
		}

		public NavElem getFather() {
			return father;
		}

		public void setFather(NavElem father) {
			this.father = father;
		}

		public List<NavElem> getSons() {
			return sons;
		}

		public void setSons(List<NavElem> sons) {
			this.sons = sons;
			hasSons = sons.size() > 0;
		}

		public void addSon(NavElem son) {
			getSons().add(son);
			hasSons = true;
		}

		public boolean isHasSons() {
			return hasSons;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
}