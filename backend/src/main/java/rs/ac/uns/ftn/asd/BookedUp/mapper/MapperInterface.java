package rs.ac.uns.ftn.asd.BookedUp.mapper;

public interface MapperInterface<T, U> {

        T toEntity(U dto);

        U toDto(T entity);

}
