package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.entity.base.BaseDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import kz.worldclass.finances.data.entity.BudgetHistoryEntity;
import kz.worldclass.finances.data.entity.BudgetHistoryItemEntity;
import kz.worldclass.finances.data.entity.BudgetNextChangeEntity;
import kz.worldclass.finances.data.entity.BudgetNextChangeItemEntity;
import kz.worldclass.finances.data.entity.DictBudgetEntity;
import kz.worldclass.finances.data.entity.DictBudgetNextChangeStateEntity;
import kz.worldclass.finances.data.entity.DictBudgetNextChangeTypeEntity;
import kz.worldclass.finances.data.entity.DictBudgetStoreTypeEntity;
import kz.worldclass.finances.data.entity.DictCurrencyEntity;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.DictRoleEntity;
import kz.worldclass.finances.data.entity.NoteEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import kz.worldclass.finances.data.entity.UserRoleLinkEntity;
import kz.worldclass.finances.data.entity.base.BaseDictEntity;
import kz.worldclass.finances.data.entity.base.BaseEntity;

public class Dtos {
    /**
     * 
     * @param source
     * @param target 
     * @throws NullPointerException target is null
     */
    public static void copy(BaseEntity source, BaseDto target) {
        Objects.requireNonNull(target, "target is null");
        if (source != null) target.id = source.getId();
    }
    
    /**
     * 
     * @param source
     * @param target 
     * @throws NullPointerException target is null
     */
    public static void copy(BaseDictEntity source, BaseDictDto target) {
        Objects.requireNonNull(target, "target is null");
        copy((BaseEntity) source, (BaseDto) target);
        if (source != null) {
            target.code = source.getCode();
            target.name = source.getName();
            target.disabled = source.getDisabled();
        }
    }
    
    public static BudgetHistoryDto less(BudgetHistoryEntity source) {
        BudgetHistoryDto result = new BudgetHistoryDto();
        copy(source, result);
        if (source != null) {
            result.changeDate = (source.getChangeDate() == null ? null : source.getChangeDate().getTime());
        }
        return result;
    }
    
    public static BudgetHistoryDto complete(BudgetHistoryEntity source) {
        BudgetHistoryDto result = less(source);
        if (source != null) {
            result.org = complete(source.getOrg());
            result.type = complete(source.getType());
            result.note = complete(source.getNote());
        }
        return result;
    }
    
    public static BudgetHistoryItemDto less(BudgetHistoryItemEntity source) {
        BudgetHistoryItemDto result = new BudgetHistoryItemDto();
        copy(source, result);
        if (source != null) {
            result.itemValue = source.getItemValue();
        }
        return result;
    }
    
    public static BudgetHistoryItemDto complete(BudgetHistoryItemEntity source) {
        BudgetHistoryItemDto result = less(source);
        if (source != null) {
            result.history = complete(source.getHistory());
            result.currency = complete(source.getCurrency());
            result.storeType = complete(source.getStoreType());
            result.budgetType = complete(source.getBudgetType());
            result.note = complete(source.getNote());
        }
        return result;
    }
    
    public static BudgetNextChangeDto less(BudgetNextChangeEntity source) {
        BudgetNextChangeDto result = new BudgetNextChangeDto();
        copy(source, result);
        if (source != null) {
            result.changeDate = (source.getChangeDate() == null ? null : source.getChangeDate().getTime());
        }
        return result;
    }
    
    public static BudgetNextChangeDto complete(BudgetNextChangeEntity source) {
        BudgetNextChangeDto result = less(source);
        if (source != null) {
            result.org = complete(source.getOrg());
            result.type = complete(source.getType());
            result.state = complete(source.getState());
            result.note = complete(source.getNote());
        }
        return result;
    }
    
    public static BudgetNextChangeItemDto less(BudgetNextChangeItemEntity source) {
        BudgetNextChangeItemDto result = new BudgetNextChangeItemDto();
        copy(source, result);
        if (source != null) {
            result.itemValue = source.getItemValue();
        }
        return result;
    }
    
    public static BudgetNextChangeItemDto complete(BudgetNextChangeItemEntity source) {
        BudgetNextChangeItemDto result = less(source);
        if (source != null) {
            result.change = complete(source.getChange());
            result.currency = complete(source.getCurrency());
            result.storeType = complete(source.getStoreType());
            result.budgetType = complete(source.getBudgetType());
            result.note = complete(source.getNote());
        }
        return result;
    }
    
    public static DictBudgetDto less(DictBudgetEntity source) {
        DictBudgetDto result = new DictBudgetDto();
        copy(source, result);
        if (source != null) {
            result.outgo = source.getOutgo();
        }
        return result;
    }
    
    public static DictBudgetDto complete(DictBudgetEntity source) {
        DictBudgetDto result = less(source);
        if (source != null) {
            result.parent = complete(source.getParent());
        }
        return result;
    }
    
    public static DictBudgetDto tree(DictBudgetEntity source) {
        DictBudgetDto result = less(source);
        if (source != null) {
            result.children = new ArrayList<>();
            for (DictBudgetEntity child: source.getChildren()) result.children.add(tree(child));
            Collections.sort(result.children, new Comparator<DictBudgetDto>() {
                @Override
                public int compare(DictBudgetDto o1, DictBudgetDto o2) {
                    if ((o1 == null) || (o1.code == null) || (o1.code.isEmpty())) {
                        if ((o2 == null) || (o2.code == null) || (o2.code.isEmpty())) return 0;
                        else return -1;
                    } else {
                        if ((o2 == null) || (o2.code == null) || (o2.code.isEmpty())) return 1;
                        else return o1.code.compareTo(o2.code);
                    }
                }
            });
        }
        return result;
    }
    
    public static DictBudgetNextChangeStateDto less(DictBudgetNextChangeStateEntity source) {
        DictBudgetNextChangeStateDto result = new DictBudgetNextChangeStateDto();
        copy(source, result);
        return result;
    }
    
    public static DictBudgetNextChangeStateDto complete(DictBudgetNextChangeStateEntity source) {
        DictBudgetNextChangeStateDto result = less(source);
        return result;
    }
    
    public static DictBudgetNextChangeTypeDto less(DictBudgetNextChangeTypeEntity source) {
        DictBudgetNextChangeTypeDto result = new DictBudgetNextChangeTypeDto();
        copy(source, result);
        return result;
    }
    
    public static DictBudgetNextChangeTypeDto complete(DictBudgetNextChangeTypeEntity source) {
        DictBudgetNextChangeTypeDto result = less(source);
        return result;
    }
    
    public static DictBudgetStoreTypeDto less(DictBudgetStoreTypeEntity source) {
        DictBudgetStoreTypeDto result = new DictBudgetStoreTypeDto();
        copy(source, result);
        return result;
    }
    
    public static DictBudgetStoreTypeDto complete(DictBudgetStoreTypeEntity source) {
        DictBudgetStoreTypeDto result = less(source);
        return result;
    }
    
    public static DictCurrencyDto less(DictCurrencyEntity source) {
        DictCurrencyDto result = new DictCurrencyDto();
        copy(source, result);
        if (source != null) {
            result.symbol = source.getSymbol();
        }
        return result;
    }
    
    public static DictCurrencyDto complete(DictCurrencyEntity source) {
        DictCurrencyDto result = less(source);
        if (source != null) {}
        return result;
    }
    
    public static DictOrgDto less(DictOrgEntity source) {
        DictOrgDto result = new DictOrgDto();
        copy(source, result);
        if (source != null) {}
        return result;
    }
    
    public static DictOrgDto complete(DictOrgEntity source) {
        DictOrgDto result = less(source);
        if (source != null) {}
        return result;
    }
    
    public static DictRoleDto less(DictRoleEntity source) {
        DictRoleDto result = new DictRoleDto();
        copy(source, result);
        if (source != null) {}
        return result;
    }
    
    public static DictRoleDto complete(DictRoleEntity source) {
        DictRoleDto result = less(source);
        if (source != null) {}
        return result;
    }
    
    public static NoteDto less(NoteEntity source) {
        NoteDto result = new NoteDto();
        copy(source, result);
        if (source != null) {
            result.noteValue = source.getNoteValue();
        }
        return result;
    }
    
    public static NoteDto complete(NoteEntity source) {
        NoteDto result = less(source);
        return result;
    }
    
    public static UserDto less(UserEntity source) {
        UserDto result = new UserDto();
        copy(source, result);
        if (source != null) {
            result.login = source.getLogin();
            result.password = source.getPassword();
            result.firstname = source.getFirstname();
            result.lastname = source.getLastname();
            result.patronymic = source.getPatronymic();
            result.email = source.getEmail();
            result.locked = source.getLocked();
        }
        return result;
    }
    
    public static UserDto complete(UserEntity source) {
        UserDto result = less(source);
        if (source != null) {
            List<DictRoleDto> roleDtos = new ArrayList<>();
            Collection<UserRoleLinkEntity> roleLinkEntitys = source.getRoleLinks();
            if (roleLinkEntitys != null) {
                for (UserRoleLinkEntity linkEntity: roleLinkEntitys) {
                    if (linkEntity != null) {
                        DictRoleEntity roleEntity = linkEntity.getRole();
                        if (roleEntity != null) roleDtos.add(complete(roleEntity));
                    }
                }
            }
            result.roles = roleDtos.toArray(new DictRoleDto[roleDtos.size()]);
            
            result.org = complete(source.getOrg());
        }
        return result;
    }
}