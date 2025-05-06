package co.uniquindio.crud.service.interfaces;

import co.uniquindio.crud.dto.PaginacionResponseDTO;
import co.uniquindio.crud.dto.ParcialUserUpdate;
import co.uniquindio.crud.dto.UsuarioDTO;
import co.uniquindio.crud.dto.UsuarioResponseDTO;

public interface UsuarioService {

    public UsuarioResponseDTO getUsuarioById(Long id);
    public PaginacionResponseDTO getAllUsuariosPaginados(int page, int size);
    public UsuarioResponseDTO createUsuario(UsuarioDTO usuarioDTO);
    public UsuarioResponseDTO updateUsuario(Long id, UsuarioDTO usuarioDTO);
    public UsuarioResponseDTO partialUpdateUsuario(Long id, ParcialUserUpdate dto) ;
    public void deleteUsuario(Long id);


}
