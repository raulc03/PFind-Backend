package com.fcorp.pfind.Servicios;

import java.util.List;

import com.fcorp.pfind.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fcorp.pfind.Repositorio.Bodega_Productorepositorio;
import com.fcorp.pfind.Repositorio.Bodegarepositorio;
import com.fcorp.pfind.Repositorio.Resenarepositorio;
import org.springframework.web.multipart.MultipartFile;

import static com.fcorp.pfind.Servicios.Decodificador.compressBytes;
import static com.fcorp.pfind.Servicios.Decodificador.decompressBytes;

@Service
public class ServicioBodega {
	@Autowired
	private Bodegarepositorio bodegaRepositorio;
	@Autowired
	private Resenarepositorio resenaRepositorio;
	@Autowired
	private Bodega_Productorepositorio bodega_productoRepositorio;
	
	public Bodega obtenerBodega(Long codigo)throws Exception {
		Bodega b;
		b = bodegaRepositorio.findById(codigo).get();
		if(b == null) throw new Exception("La bodega no existe");
		return b;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Bodega registrarBodega(Bodega bodega) {
		return bodegaRepositorio.save(bodega);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Resena registrarResena(Resena Rinput) throws Exception {
		Resena r = null;
		r = Rinput;
		if (r == null){ throw new Exception("No se pudo registrar");}
		else{
			return resenaRepositorio.save(Rinput);
		}
	}

	public List<Resena> obtenerResenas(){
		return resenaRepositorio.findAll();
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Bodega actualizarBodega(Bodega bodega) throws Exception {
		bodegaRepositorio.findById(bodega.getCodigo()).orElseThrow(() -> new Exception("No se encontró esa bodega"));
		return bodegaRepositorio.save(bodega);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Bodega_Producto registrarBodega_Producto(Bodega_Producto BPinput, Long id) throws Exception {
		Bodega_Producto b = null;
		b = BPinput;
		Bodega bodega = null;
		bodega = obtenerBodega(id);
		b.setBodega(bodega);
		if (b == null){ throw new Exception("No se pudo registrar");}
		else{
			return bodega_productoRepositorio.save(b);
		}
	}

	public Bodega_Producto obtenerBodegaProducto(Long id) throws Exception {
		Bodega_Producto bp = bodega_productoRepositorio.findById(id).get();
		if(bp == null){
			throw new Exception("No se pudo encontrar lo que busca");
		}else{
			return bp;
		}
	}



	public void borrarBodegaProducto(Long id ){
		bodega_productoRepositorio.deleteById(id);
	}

	public List<Bodega_Producto> obtenerBPporBodega(Long id) {
		return bodega_productoRepositorio.busquedaPorBodega(id);
	}

	public List<Bodega_Producto> obtenerBPporCategoria(Long cid) {
		return bodega_productoRepositorio.buscarPorCategoria(cid);
	}
	public List<Bodega_Producto> obtenerBPporNombre(String nombre) {
		return bodega_productoRepositorio.busquedaFinal(nombre);
	}
	
	public List<Bodega_Producto> obtenerBPporMaxMin(Double pmin, Double pmax){
		return bodega_productoRepositorio.buscarPorRango(pmin, pmax);
	}
	@Transactional(rollbackFor = Exception.class)
    public Bodega_Producto actualizarBodega_Producto(Bodega_Producto BPinput) throws Exception {
        Bodega_Producto bp = bodega_productoRepositorio.findById(BPinput.getCodigo()).orElseThrow(()-> new Exception("No se encontró bodega_producto"));
        bp.setCodigo(BPinput.getCodigo());
        bp.setBodega(BPinput.getBodega());
        bp.setProducto(BPinput.getProducto());
        return bodega_productoRepositorio.save(bp);
    }

	public List<Bodega_Producto> obtenerBPporNombreMarcaYBodega(String nombre, String marca, String bodega) {
		return bodega_productoRepositorio.buscarPorNombreMarcaYBodega(nombre, marca, bodega);
	}
	
    public List<Bodega> obtenerBodegas(){
		return bodegaRepositorio.findAll();
	}

	public void cargarImagen(MultipartFile file) throws Exception {
		System.out.println("Original Image Byte Size - " + file.getBytes().length);
		Bodega b = null;
		int n = bodegaRepositorio.findAll().size();
		b = bodegaRepositorio.findAll().get(n-1);
		if(b == null){
			System.out.println("No se encontró la Bodega");
			throw new Exception("No se encontró la Bodega que busca");
		}
		else {
			byte[] new_image = file.getBytes();
			b.setImagen(new_image);
			System.out.println("Imagen de la Bodega Guardada");
			bodegaRepositorio.save(b);
		}
	}

	public Bodega_Producto cargarImagenProducto(MultipartFile file, Long id, Long bpid) throws Exception {
		System.out.println("Original Image Byte Size - " + file.getBytes().length);
		Bodega_Producto bp = null;
		Bodega b = null;
		b = obtenerBodega(id);
		bp = obtenerBodegaProducto(bpid);
		if(bp == null){
			System.out.println("No se encontró la Bodega");
			throw new Exception("No se encontró la Bodega que busca");
		}
		else {
			byte[] new_image = file.getBytes();
			bp.setImagen(new_image);
			System.out.println("Imagen de la Bodega Guardada");
			return bodega_productoRepositorio.save(bp);
		}
	}

	public Bodega getBodegaImagen(Long id) throws Exception {
		Bodega b = null;
		b = obtenerBodega(id);
		b.setImagen(decompressBytes(b.getImagen()));
		if(b == null){
			throw new Exception("No se pudo encontrar la Bodega");
		}else {
			//System.out.println(b.getNombre());
			return b;
		}
	}

	public byte[] getBodegaImagenProducto(Long id, Long bpi) throws Exception{
		Bodega b = null;
		b = obtenerBodega(id);
		Bodega_Producto bp = null;
		for(int i = 0; i < b.getProductos().size(); i++){
			if(b.getProductos().get(i).getCodigo() == bpi){
				System.out.println(b.getProductos().get(i));
				bp = b.getProductos().get(i);
			}
		}
		bp.setImagen(decompressBytes(bp.getImagen()));
		if(bp == null){
			throw new Exception("No se pudo encontrar el Producto");
		}else{
			return bp.getImagen();
		}
	}

	public byte[] downloadImage(Long idBodega){
		Bodega b = bodegaRepositorio.findById(idBodega).get();
		return b.getImagen();
	}
}
