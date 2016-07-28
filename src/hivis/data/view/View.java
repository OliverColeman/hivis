/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */
package hivis.data.view;

/**
 * Interface for classes that present a view of some data.
 *
 * @author O. J. Coleman
 */
public interface View {
	/**
	 * Update this view. If the view does not cache the view it presents then it may not be necessary to do anything in this method.
	 * Note that views based on a {@link DataSet} or subclasses thereof will generally update themselves automatically when the
	 * underlying DataSet changes. This method is for handling the case where the view is also affected by an external factor, such
	 * as parameters to a function that may change.
	 * 
	 * @param cause Optional argument for specifying the reason for updating the view. Implementation specific.
	 */
	void updateView(Object cause);
}